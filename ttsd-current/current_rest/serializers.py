# -*- coding: utf-8 -*-
import logging
import re
from datetime import datetime, timedelta

from django.db.models import Sum
from rest_framework import serializers, status
from rest_framework.response import Response

from current_rest import constants
from current_rest import models
from current_rest.models import Loan, Agent
from current_rest import constants, models
from current_rest.biz import PERSONAL_MAX_DEPOSIT
from current_rest.biz.current_account_manager import CurrentAccountManager
from current_rest.biz.current_daily_manager import CurrentDailyManager

logger = logging.getLogger(__name__)


def json_validation_required(serializer):
    def decorator(func):
        def wrapper(request, *args, **kwargs):
            serializer_instance = serializer(data=request.data)
            if serializer_instance.is_valid():
                return func(request, serializer_instance.validated_data, *args, **kwargs)
            logger.error('request data is illegal. data is {}'.format(request.data))
            return Response(status=status.HTTP_400_BAD_REQUEST)

        return wrapper

    return decorator


class AccountSerializer(serializers.ModelSerializer):
    created_time = serializers.DateTimeField(format='%Y-%m-%d %H:%M:%S')
    updated_time = serializers.DateTimeField(format='%Y-%m-%d %H:%M:%S')
    personal_max_deposit = serializers.SerializerMethodField()

    def get_personal_max_deposit(self, instance):
        user_max_deposit = PERSONAL_MAX_DEPOSIT - instance.balance if PERSONAL_MAX_DEPOSIT - instance.balance > 0 else 0
        today_sum_deposit = self.__calculate_success_deposit_today()
        current_daily_amount = CurrentDailyManager().get_current_daily_amount()
        return min(user_max_deposit, current_daily_amount - today_sum_deposit)

    @staticmethod
    def __calculate_success_deposit_today():
        today = datetime.now().date()
        tomorrow = today + timedelta(1)
        amount_sum = models.CurrentDeposit.objects.filter(status=constants.DEPOSIT_SUCCESS,
                                                          updated_time__range=(today, tomorrow)) \
            .all().aggregate(Sum('amount')) \
            .get('amount__sum', 0)
        return amount_sum if amount_sum else 0

    class Meta:
        model = models.CurrentAccount
        fields = ('id', 'login_name', 'balance', 'created_time', 'updated_time', 'personal_max_deposit')
        read_only_fields = ('login_name', 'balance', 'created_time', 'updated_time', 'personal_max_deposit')


class DepositSerializer(serializers.ModelSerializer):
    login_name = serializers.RegexField(regex=re.compile('[A-Za-z0-9_]{6,25}'))
    amount = serializers.IntegerField(min_value=0)
    source = serializers.ChoiceField(choices=constants.SOURCE_CHOICE)
    no_password = serializers.BooleanField()

    def create(self, validated_data):
        current_account = CurrentAccountManager().fetch_account(login_name=validated_data.get('login_name'))
        validated_data['current_account'] = current_account
        return super(DepositSerializer, self).create(validated_data=validated_data)


class DepositSuccessSerializer(serializers.Serializer):
    order_id = serializers.IntegerField(min_value=0, required=True)
    success = serializers.BooleanField(required=True)

    class Meta:
        model = models.CurrentDeposit
        fields = ('id', 'login_name', 'amount', 'source', 'no_password', 'status')


class LoanSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField(required=False)
    serial_number = serializers.IntegerField()
    agent = serializers.PrimaryKeyRelatedField(queryset=Agent.objects.all())
    amount = serializers.IntegerField(min_value=0, max_value=99999)
    loan_type = serializers.ChoiceField(choices=constants.LOAN_TYPE_CHOICES)
    debtor = serializers.RegexField(regex=re.compile('[A-Za-z0-9]{6,25}'))
    debtor_identity_card = serializers.CharField(max_length=18)
    effective_date = serializers.DateTimeField(format='%Y-%m-%d %H:%M:%S')
    expiration_date = serializers.DateTimeField(format='%Y-%m-%d %H:%M:%S')
    status = serializers.ChoiceField(choices=constants.LOAN_STATUS_CHOICES)
    creator = serializers.RegexField(regex=re.compile('[A-Za-z0-9]{6,25}'), required=False)
    auditor = serializers.RegexField(regex=re.compile('[A-Za-z0-9]{6,25}'), required=False)

    class Meta:
        model = models.Loan
        fields = ('id', 'serial_number', 'agent', 'amount', 'loan_type', 'debtor', 'debtor_identity_card',
                  'effective_date', 'expiration_date', 'status', 'create_time', 'update_time', 'creator', 'auditor')
