# coding=utf-8
from datetime import datetime
from django.http import JsonResponse
from django.shortcuts import render
from django.views.decorators.http import require_http_methods
from rest_framework import status

from current_console import constants
from current_console.decorators import user_roles_check
from current_console.rest_client import RestClient


@require_http_methods(["GET"])
@user_roles_check(['ADMIN', 'OPERATOR', 'OPERATOR_ADMIN'])
def redeem_list(request):
    redeem_list = RestClient('redeem-list').get(params=request.GET)
    sumAmount = 0
    if redeem_list['results']:
        for redeem in redeem_list['results']:
            sumAmount += redeem['amount']
            redeem['amount'] = '{0:.2f}'.format(redeem['amount']/100.0)
    return render(request, 'console/redeem/list.html',
                  {'redeem_list': redeem_list,
                   'types': constants.REDEEM_STATUS_CHOICE,
                   'sumAmount': '{0:.2f}'.format(sumAmount/100.0),
                   'page': request.GET.get('page')})


@require_http_methods(["PUT"])
@user_roles_check(['ADMIN', 'OPERATOR', 'OPERATOR_ADMIN'])
def audit_redeem(request, result, pk):
    data = {
        'auditor': 'auditor'
    }

    response = RestClient('redeem-audit/{}/{}'.format(pk, result)).put(data=data)

    if response:
        return JsonResponse({'message': response[1]}, status=status.HTTP_200_OK)

    return JsonResponse({'message', '內部服务器错误'}, status=status.HTTP_200_OK)