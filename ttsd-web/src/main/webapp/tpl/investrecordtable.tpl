<table class="invest-list">
    <thead>
        <tr>
            <th>交易时间</th>
            <th>交易详情</th>
            <th>交易状态</th>
            <th>下次回款</th>
            <th>我的投资</th>
            <th>操作</th>
        </tr>
    </thead>
    <tbody>
       {{#data.records}}
        <tr>
            <td>{{createdTime}}</td>
            <td><a class="red" href="/loan/{{loanId}}">{{loanName}}</td>
            <td>{{investStatus}}</td>
            <td>
            {{#nextRepayDay}}{{nextRepayDay}} / {{nextRepayAmount}}元{{/nextRepayDay}}
            {{^nextRepayDay}}/{{/nextRepayDay}}
            </td>
            <td>{{amount}}元</td>
            <td>
            {{#hasContract}}
            <span class="plan" data-repay="/investor/query-invest-repay?investId={{id}}">回款记录</span> |
            <a class="red" href="/contract/investor/{{loanId}}" target="_blank">合同</a>
            {{/hasContract}}
            {{^hasContract}} - / - {{/hasContract}}
            </td>
        </tr>
        {{/data.records}}
        {{^data.records}}
         <td colspan="6" class="txtc">暂时没有投资纪录</td>
        {{/data.records}}
    </tbody>
</table>

<div class="pagination" data-currentpage="{{data.index}}">
    <span class="total">共 {{data.count}} 条记录，当前第 {{data.index}} 页</span>
    {{#data.hasPreviousPage}}<a href="javascript:;" class="prevPage">上一页</a>{{/data.hasPreviousPage}}
    {{#data.hasNextPage}}<a href="javascript:;" class="nextPage">下一页</a>{{/data.hasNextPage}}
</div>

