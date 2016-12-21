# coding:utf-8

import uuid
import time

from tornado import gen
from tornado.options import define, options, parse_command_line
import tornado.ioloop
from tornado.web import RequestHandler, HTTPError
import torndb

define("port", default=8888, help="run on the given port", type=int)
define("mysql_host", default="192.168.1.151:3306", help="database host")
define("mysql_database", default="aa", help="database name")
define("mysql_user", default="root", help="database user")
define("mysql_password", default="root", help="database password")

define("wdzj_username", default="test", help="wdzj api username")
define("wdzj_password", default="test", help="wdzj api password")


class Error404Handler(tornado.web.ErrorHandler):
    def write_error(self, *args, **kwargs):
        self.write({'error': "Url not found"})


class RefreshTokenHanlder(RequestHandler):
    def get(self):
        username, password = self.get_argument('username', None), self.get_argument('password', None)
        if username and password:
            if username != options.wdzj_username or password != options.wdzj_password:
                raise HTTPError(403)
            else:
                uuid1 = str(uuid.uuid1())
                now_time = str(int(round(time.time() * 1000)))
                self.set_secure_cookie('secKey', uuid1 + ':' + now_time)
                self.write({'data': {'token': uuid1}})
        else:
            raise HTTPError(400)


class BaseHandler(RequestHandler):
    @gen.coroutine
    def prepare(self):
        token = self.get_argument('token', None)
        sec_vals = str(self.get_secure_cookie('secKey')).split(":")
        if len(sec_vals) != 2:
            raise HTTPError(403)

        cached_token_val = sec_vals[0]
        cached_token_time = int(sec_vals[-1])
        now_time = int(round(time.time() * 1000))

        if not token or now_time - cached_token_time > 3600 * 1000 or cached_token_val != token:
            raise HTTPError(403)

    def write_error(self, status_code, **kwargs):
        known_errors = {400: "Parameters error", 403: "Token validation error"}
        if status_code in known_errors:
            self.write({'error': known_errors.get(status_code)})


class LoanDetailHandler(BaseHandler):
    totalCountSQL = "select count(1) as totalCount from loan where DATE_FORMAT(raising_complete_time, '%%Y-%%m-%%d') = %s"

    totalAmountSQL = "select IFNULL(sum(loan_amount),0) totalAmount from loan where DATE_FORMAT(raising_complete_time, '%%Y-%%m-%%d') = %s"

    loanDetailSQL = '''
        select id as projectId, `name` as title, loan_amount as amount, 100 as schedule, CONCAT((base_rate + activity_rate)*100,'%%') as interestRate, periods as deadline,
        '月' as deadlineUnit, 0 as reward, '抵押标' as type, type as repaymentType, agent_login_name as userName, raising_complete_time as successTime
        from loan where DATE_FORMAT(raising_complete_time,'%%Y-%%m-%%d') = %s limit %s, %s;
    '''

    investDetailSQL = '''
        select UPPER(md5(login_name)) as subscribeUserName, CAST(truncate(amount/100,2) as char(10)) as amount, cast(truncate(amount/100,2) as char(10)) as validAmount,
        CAST(invest_time as CHAR(19)) as addDate, 1 as status, 0 as type from invest where loan_id = %s and status = 'SUCCESS'
    '''

    def get(self):
        try:
            current_page = int(self.get_argument('page', None))
            page_size = int(self.get_argument('pageSize', None))
            date = self.get_argument('date', None)
        except ValueError:
            raise HTTPError(400)

        start = (current_page - 1) * page_size

        total_count = int(self.settings['db'].get(self.totalCountSQL, date)["totalCount"])
        total_amount = float(self.settings['db'].get(self.totalAmountSQL, date)["totalAmount"]) / 100
        loan_detail_rows = self.settings['db'].query(self.loanDetailSQL, date, start, page_size)

        for row in loan_detail_rows:
            loan_id = str(row['projectId'])
            if row['repaymentType'] == 'LOAN_INTEREST_MONTHLY_REPAY' \
                    or row['repaymentType'] == 'INVEST_INTEREST_MONTHLY_REPAY':
                row['repaymentType'] = 5
            else:
                row['repaymentType'] = 1
            row['loanUrl'] = 'https://tuotiansudai.com/loan/' + loan_id
            row['successTime'] = row['successTime'].strftime("%Y-%m-%d %H:%M:%S")
            row['subscribes'] = self.settings['db'].query(self.investDetailSQL, loan_id)

        total_page = ((total_count - 1) / page_size + 1) or 1

        return self.write(
            {'totalPage': total_page, 'currentPage': current_page, 'totalCount': total_count,
             'totalAmount': total_amount,
             'date': date, 'borrowList': loan_detail_rows})


if __name__ == '__main__':
    parse_command_line()

    # Have one global connection to the blog DB across all handlers
    db = torndb.Connection(
        host=options.mysql_host, database=options.mysql_database,
        user=options.mysql_user, password=options.mysql_password)

    secure_key = str(uuid.uuid1())

    settings = {'debug': True, 'db': db, 'default_handler_class': Error404Handler,
                'default_handler_args': dict(status_code=404)}
    app = tornado.web.Application([
        (r'/wdzj/refreshToken', RefreshTokenHanlder),
        (r'/wdzj/loanDetail', LoanDetailHandler)], cookie_secret=secure_key, **settings)
    app.listen(options.port)
    tornado.ioloop.IOLoop.current().start()
