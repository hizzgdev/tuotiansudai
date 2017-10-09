# coding=utf-8
import hashlib
import json
import re
import time
import uuid

import redis
from sqlalchemy import func

import settings
from logging_config import logger
from models import User, db, UserRole
from producer import producer

pool = redis.ConnectionPool(host=settings.REDIS_HOST, port=settings.REDIS_PORT, db=settings.REDIS_DB)
CAPTCHA_FORMAT = u"CAPTCHA:LOGIN:{0}"
TOKEN_FORMAT = u"TOKEN:{0}"
LOGIN_FAILED_TIMES_FORMAT = u'LOGIN_FAILED_TIMES:{0}'

USER_LOGIN_NAME_REGEX = re.compile(r"(?!^\d+$)^\w{5,25}$")
USER_MOBILE_REGEX = re.compile(r"^1\d{10}$")
USER_PASSWORD_REGEX = re.compile(r"^(?=.*[^\d])(.{6,20})$")


class SessionManager(object):
    def __init__(self, source='WEB'):
        self.connection = redis.Redis(connection_pool=pool)
        self.expire_seconds = settings.WEB_TOKEN_EXPIRED_SECONDS if source.upper() == 'WEB' else settings.MOBILE_TOKEN_EXPIRED_SECONDS
        self.source = source

    def get(self, session_id):
        token_key = TOKEN_FORMAT.format(session_id)
        user_info = self.connection.get(token_key)
        if user_info:
            self.connection.expire(token_key, self.expire_seconds)
            return json.loads(user_info)

    def set(self, data, old_session_id):
        new_token_id = self._generate_token_id()
        token_key = TOKEN_FORMAT.format(new_token_id)
        self.connection.setex(token_key, json.dumps(data), self.expire_seconds)
        old_token_key = TOKEN_FORMAT.format(old_session_id)
        self.connection.delete(old_token_key)
        return new_token_id

    def _generate_token_id(self):
        return uuid.uuid4()

    def create(self):
        return self._generate_token_id()

    def delete(self, session_id):
        user_info = self.get(session_id)
        self.connection.delete(TOKEN_FORMAT.format(session_id))
        return user_info

    def refresh(self, session_id):
        """
        Only used by app
        """
        old_token = TOKEN_FORMAT.format(session_id)
        data = self.connection.get(old_token)
        if data:
            new_token_id = self._generate_token_id()
            new_token = TOKEN_FORMAT.format(new_token_id)
            self.connection.setex(new_token, data, self.expire_seconds)
            self.connection.delete(old_token)
            user_info = json.loads(data)
            update_last_login_time_source(user_info['login_name'], self.source)
            return new_token_id


class UsernamePasswordError(Exception):
    pass


class UserNotExistedError(Exception):
    pass


class UserBannedError(Exception):
    pass


class LoginManager(object):
    def __init__(self, form, ip_address="127.0.0.1"):
        self.ip_address = ip_address.split(',')[0] if ip_address else None
        self.form = form
        self.connection = redis.Redis(connection_pool=pool)
        self.session_manager = SessionManager(source=self.form.source.data)
        logger.debug("x-forwarded-for:{}".format(ip_address))

    def _is_password_valid(self, user):
        return user and user.password == hashlib.sha1(
            u"%s{%s}" % (hashlib.sha1(self.form.password.data.encode('utf-8')).hexdigest(), user.salt)).hexdigest()

    def _load_user(self):
        user = User.query.filter(
            (User.login_name == self.form.username.data) | (User.mobile == self.form.username.data)).first()
        if not user:
            logger.debug(u"{} not exist".format(self.form.username.data))
            raise UserNotExistedError()
        return user

    def _success(self, user):
        user_info = {'login_name': user.login_name, 'mobile': user.mobile, 'roles': [role.role for role in user.roles]}
        new_token_id = self.session_manager.set(user_info, self.form.token.data)
        logger.info(u"{} login successful. source: {}, token_id: {}, user_info: {}".format(self.form.username.data,
                                                                                           self.form.source.data,
                                                                                           new_token_id, user_info))
        return new_token_id, user_info

    def _increase_failed_times(self):
        login_failed_times_key = LOGIN_FAILED_TIMES_FORMAT.format(self.form.username.data)
        self.connection.incr(login_failed_times_key)
        self.connection.expire(login_failed_times_key, settings.LOGIN_FAILED_EXPIRED_SECONDS)
        logger.info(u"{} login failed. source: {}".format(self.form.username.data, self.form.source.data))

    def _get_user_info(self):
        user = self._load_user()
        if self._is_password_valid(user):
            return self._success(user)

    def _normal_login(self):
        login_failed_times_key = LOGIN_FAILED_TIMES_FORMAT.format(self.form.username.data)
        failed_times = int(self.connection.get(login_failed_times_key)) if self.connection.get(
            login_failed_times_key) else 0
        if failed_times >= settings.LOGIN_FAILED_MAXIMAL_TIMES:
            logger.debug(u"{} have been locked. source: {}".format(self.form.username.data, self.form.source.data))
            raise UserBannedError()

        info = self._get_user_info()
        if not info:
            raise UsernamePasswordError()
        return info

    def _fail_login(self, message, save_log=True):
        if save_log:
            self._save_log(False)
        self._increase_failed_times()
        return self._render(False, message=message)

    def _success_login(self, user_info, token):
        self._save_log(True)
        update_last_login_time_source(user_info['login_name'], self.form.source.data)
        return self._render(True, user_info=user_info, token=token)

    @staticmethod
    def _render(is_success, message=None, user_info=None, token=None):
        return {'result': is_success, 'message': message, 'user_info': user_info, 'token': token}

    def login(self):
        try:
            token_id, user_info = self._normal_login()
            return self._success_login(user_info, token_id)
        except UsernamePasswordError:
            return self._fail_login(message='用户名或密码错误')
        except UserNotExistedError:
            return self._fail_login(message='用户名或密码错误', save_log=False)
        except UserBannedError:
            return self._fail_login(message='用户已被禁用')

    def no_password_login(self):
        try:
            user = self._load_user()
            token_id, user_info = self._success(user)
            return self._success_login(user_info, token_id)
        except UserNotExistedError:
            return self._fail_login('用户不存在', save_log=False)

    def _save_log(self, is_success):
        login_log = dict(
            loginName=self.form.username.data,
            source=self.form.source.data,
            ip=self.ip_address,
            device=self.form.device_id.data,
            loginTime=int(time.time() * 1000),
            success=is_success
        )
        producer.send_message(json.dumps(login_log))


def update_last_login_time_source(username, source):
    user = User.query.filter((User.login_name == username)).first()
    user.last_login_time = func.now()
    user.last_login_source = source
    db.session.commit()


def active(username):
    login_failed_times_key = LOGIN_FAILED_TIMES_FORMAT.format(username)
    conn = redis.Redis(connection_pool=pool)
    conn.delete(login_failed_times_key)


class UserFieldMissingError(Exception):
    def __init__(self, field):
        self.message = '{} missing'.format(field)


class UserFieldValidationError(Exception):
    def __init__(self, field):
        self.message = '{} validate failed'.format(field)


class UserService(object):
    REGISTER_REQUIRED_FIELDS = ('login_name', 'mobile', 'password', 'referrer', 'channel', 'source')
    EDITABLE_FIELDS = ('password', 'salt', 'email', 'user_name', 'identity_number',
                       'last_modified_time', 'last_modified_user', 'avatar', 'referrer',
                       'status', 'channel', 'province', 'city', 'source', 'experience_balance')

    def create(self, json_data):
        """ json : dict of {login_name, mobile, password, referrer, channel, source} """
        for field_name in UserService.REGISTER_REQUIRED_FIELDS:
            if field_name not in json_data:
                raise UserFieldMissingError(field_name)

        login_name = json_data['login_name']
        mobile = json_data['mobile']
        raw_password = json_data['password']

        if not USER_LOGIN_NAME_REGEX.match(login_name):
            raise UserFieldValidationError('login_name')

        if not USER_MOBILE_REGEX.match(mobile):
            raise UserFieldValidationError('mobile')

        if not USER_PASSWORD_REGEX.match(raw_password):
            raise UserFieldValidationError('password')

        salt = self._gen_salt()
        password = self._salt_password(salt, raw_password)
        u = User(login_name, mobile, password, salt, json_data['referrer'], json_data['channel'], json_data['source'])
        ur = UserRole(login_name, 'USER')
        db.session.add(u)
        db.session.add(ur)
        db.session.commit()
        return u.as_dict()

    def update(self, json_data):
        if 'login_name' not in json_data:
            raise UserFieldMissingError('login_name')
        user = User.query.filter((User.login_name == json_data['login_name'])).first()
        for field_name in UserService.EDITABLE_FIELDS:
            if field_name in json_data:
                setattr(user, field_name, json_data[field_name])
        db.session.commit()
        return user.as_dict()

    def _gen_salt(self):
        return uuid.uuid4().hex

    def _salt_password(self, salt, raw_password):
        return hashlib.sha1(u"%s{%s}" % (hashlib.sha1(raw_password.encode('utf-8')).hexdigest(), salt)).hexdigest()

    def find_by_login_name(self, login_name):
        user = User.query.filter((User.login_name == login_name)).first()
        return user.as_dict() if user else None

    def find_by_mobile(self, mobile):
        user = User.query.filter((User.mobile == mobile)).first()
        return user.as_dict() if user else None

    def find_by_login_name_or_mobile(self, login_name_or_mobile):
        user = User.query.filter(
            (User.login_name == login_name_or_mobile) | (User.mobile == login_name_or_mobile)).first()
        return user.as_dict() if user else None
