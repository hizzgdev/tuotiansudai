require(['jquery', 'bootstrap', 'Validform', 'Validform_Datatype', 'bootstrapSelect', 'bootstrapDatetimepicker', 'jquery-ui', 'csrf'], function ($) {
    $(function () {
        var $selectDom = $('.selectpicker'); //select表单
        var $submitBtn = $('.submit-btn'); //提交按钮
        var $importBtn = $('.file-btn'); //导入用户按钮
        //渲染select表单
        $selectDom.selectpicker();

        $('#datetimepicker1').datetimepicker({format: 'YYYY-MM-DD HH:mm:ss'});
        $('#datetimepicker2').datetimepicker({format: 'YYYY-MM-DD HH:mm:ss'});

        //导入用户按钮
        $importBtn.change(function () {
            if ("IMPORT_USER" == $('.jq-userGroup').val()) {
                var file = $(this).find('input').get(0).files[0];
                var formData = new FormData();
                var importUsersId = $('.importUsersId').val();
                formData.append('file', file);
                $.ajax({
                    url: '/membership-manage/give/import-users/' + importUsersId,
                    type: 'POST',
                    data: formData,
                    dataType: 'JSON',
                    contentType: false,
                    processData: false
                }).done(function (data) {
                    if (data.data.status) {
                        alert(data.data.message);
                        $('#importUsersId').val(data.data.message);
                    } else {
                        alert(data.data.message);
                    }
                });
            } else {
                alert("只有\"导入用户\"才能导入");
                return;
            }
        });

        function checkFormat() {
            var membershipGiveId = document.getElementById('membershipGiveId').value;
            var userGroup = $('.jq-userGroup').val();
            var startTime = $('.jq-start-date').val();
            var endTime = $('.jq-end-date').val();
            var validPeriod = $('.jq-valid-period').val();

            if ("NEW_REGISTERED_USER" == userGroup) {
                if (null == startTime || "" == startTime ||
                    null == endTime || "" == endTime) {
                    alert("新注册用户必须填写开始领取时间和结束领取时间");
                    return false;
                }
            }

            if (!/^\d+$/.test(validPeriod)) {
                alert("有效期必须是数字");
                return false;
            }

            return true;
        }

        //提交表单
        $submitBtn.on('click', function () {
            if (!checkFormat()) {
                return;
            }
            if (!confirm("确认要执行此操作吗?")) {
                return;
            }
            var membershipGiveId = document.getElementById('membershipGiveId').value;
            var userGroup = $('.jq-userGroup').val();
            var membershipLevel = $('.jq-membershipLevel').val();
            var startTime = $('.jq-start-date').val();
            var endTime = $('.jq-end-date').val();
            var validPeriod = $('.jq-valid-period').val();
            var smsNotify = $('.jq-smsNotify').get(0).checked;

            var importUsersId = document.getElementById('importUsersId').value;

            var url = "/membership-manage/give/edit?importUsersId=" + importUsersId;
            if ("IMPORT_USER" == userGroup) {
                var dataDto = {
                    "id": membershipGiveId,
                    "membershipLevel": membershipLevel,
                    "deadline": validPeriod,
                    "startTime": null,
                    "endTime": null,
                    "userGroup": userGroup,
                    "smsNotify": smsNotify,
                    "active": false
                };
            } else if ("NEW_REGISTERED_USER" == userGroup) {
                var dataDto = {
                    "id": membershipGiveId,
                    "membershipLevel": membershipLevel,
                    "deadline": validPeriod,
                    "startTime": startTime,
                    "endTime": endTime,
                    "userGroup": userGroup,
                    "smsNotify": smsNotify,
                    "active": false
                };
            }
            var dataForm = JSON.stringify(dataDto);
            $.ajax({
                url: url,
                type: 'POST',
                dataType: 'json',
                data: dataForm,
                contentType: 'application/json; charset=UTF-8'
            }).done(function (res) {
                if (res.data.status) {
                    location.href = '/membership-manage/give/list';
                } else {
                    alert(res.data.message);
                }
            });
        });
    });
});