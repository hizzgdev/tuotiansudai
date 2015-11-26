require(['jquery', 'mustache', 'text!/tpl/investor-invest-table.mustache', 'text!/tpl/investor-invest-repay-table.mustache', 'moment', 'pagination', 'layerWrapper', 'daterangepicker'], function ($, Mustache, investListTemplate, investRepayTemplate, moment, pagination, layer) {
    var today = moment().format('YYYY-MM-DD'), // 今天
        week = moment().subtract(1, 'week').format('YYYY-MM-DD'),
        month = moment().subtract(1, 'month').format('YYYY-MM-DD'),
        sixMonths = moment().subtract(6, 'month').format('YYYY-MM-DD'),
        $dateFilter = $('.date-filter'),
        $statusFilter=$('.status-filter');


    var dataPickerElement = $('#date-picker'),
        paginationElement = $('.pagination');

    dataPickerElement.dateRangePicker({separator: ' ~ '});

    var changeDatePicker = function () {
        var duration = $(".date-filter .select-item.current").data('day');
        switch (duration) {
            case 1:
                dataPickerElement.val(today + '~' + today);
                break;
            case 7:
                dataPickerElement.val(week + '~' + today);
                break;
            case 30:
                dataPickerElement.val(month + '~' + today);
                break;
            case 180:
                dataPickerElement.val(sixMonths + '~' + today);
                break;
            default:
                dataPickerElement.val('');
        }
    };

    var loadLoanData = function (currentPage) {
        var dates = dataPickerElement.val().split('~');
        var startTime = $.trim(dates[0]) || '';
        var endTime = $.trim(dates[1]) || '';
        var status = $('.status-filter .select-item.current').data('status');

        var requestData = {startTime: startTime, endTime: endTime, status: status, index: currentPage || 1};

        paginationElement.loadPagination(requestData, function (data) {
            var html = Mustache.render(investListTemplate, data);
            $('.invest-list').html(html);
            $('.invest-list .show-invest-repay').click(function () {
                $.ajax({
                    url: $(this).data('url'),
                    type: 'get',
                    dataType: 'json',
                    contentType: 'application/json; charset=UTF-8'
                }).success(function (response) {
                    var data = response.data;
                    data.isLoanCompleted = status == 'COMPLETE';
                    data.csrfToken = $("meta[name='_csrf']").attr("content");
                    if (data.status) {
                        _.each(data.records, function (item) {
                            data.loanId = item.loanId;
                            switch (item.loanRepayStatus) {
                                case 'REPAYING':
                                    item.status = '待还';
                                    break;
                                case 'COMPLETE':
                                    item.status = '完成';
                                    break;
                                case 'CANCEL':
                                    item.status = '流标';
                                    break;
                                case 'CONFIRMING':
                                    item.status = '确认中';
                                    break;
                            }
                        });
                        var html = Mustache.render(investRepayTemplate, data);

                        layer.open({
                            type: 1,
                            title: false,
                            offset: '100px',
                            area: ['1000px'],
                            shadeClose: true,
                            content: html
                        });

                    }
                });
            });
        });

        $('.invest-list').delegate('a.loan-name-col','mouseover',function() {
            layer.closeAll('tips');
            layer.tips(this.innerHTML, this, {
                tips: [1, '#efbf5c'],
                time: 2000,
                tipsMore: true,
                area: 'auto',
                maxWidth: '500'
            });
        })
    };

    loadLoanData();

    $dateFilter.find(".select-item").click(function () {
        $(this).addClass("current").siblings(".select-item").removeClass("current");
        changeDatePicker();
        loadLoanData();
    });
    //$statusFilter
    $dateFilter.find(".select-item").eq(2).trigger('click');

    //define calendar
    $('.apply-btn').click(function () {
        loadLoanData();
    });
});
