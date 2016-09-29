require(['jquery', 'underscore', 'jquery-ui', 'bootstrap', 'bootstrapSelect', 'bootstrapDatetimepicker'], function ($, _) {
    $('.selectpicker').selectpicker();

    $('#datetimepicker1').datetimepicker({format: 'YYYY-MM-DD'});
    $('#datetimepicker2').datetimepicker({format: 'YYYY-MM-DD'});

    $("select[name='prizeType']").change(function (){
        var self = $(this);
        $.ajax({
            url: '/activity-console/activity-manage/category?activityCategory=' + self.val(),
            type: 'GET',
            dataType: 'json'
        }).done(function(data) {
            var $autumnPrizeDiv=$("#autumnPrizeDiv"),
                optionList=[];

            for(var i=0;i<data.length;i++)
            {
                if(i == 0){
                    optionList.push("<option value=''>全部</option>");
                }
                optionList.push("<option value="+data[i].lotteryPrize+">"+data[i].lotteryPrizeName+"</option>");
            }
            $autumnPrizeDiv.find('select').empty().append(optionList.join(''));

            $autumnPrizeDiv.find('select').change().selectpicker('refresh');

        });
    });
});
