require('activityStyle/wechat/start_work.scss');
let commonFun = require('publicJs/commonFun');
let ifClickBtn = false;
let sourceKind = globalFun.parseURL(location.href);
(function (doc, win) {
    var docEl = doc.documentElement,
        resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',
        recalc = function () {
            var clientWidth = docEl.clientWidth;
            if (!clientWidth) return;
            var fSize = 20 * (clientWidth /375);
            fSize > 40 && (fSize = 39.36);
            docEl.style.fontSize = fSize + 'px';
        };

    if (!doc.addEventListener) return;
    win.addEventListener(resizeEvt, recalc, false);
    doc.addEventListener('DOMContentLoaded', recalc, false);
    $('body').css('visibility', 'visible');
})(document, window);

let isGet = $('.container').data('get');
let startTime = $('.container').data('startTime');
let overTime = $('.container').data('overTime');
let activityTime = new Date(startTime.replace(/-/g, "/")).getTime(); // 活动开始时间
let activityOverTime = new Date(overTime.replace(/-/g, "/")).getTime();  // 活动结束时间

if (!isGet) {
    $('.get_it_btn').show();
}
else {
    $('.got_it_btn').show();
}

$('.get_it_btn').on('click',function () {
    if (!ifClickBtn) {
        ifClickBtn = true;
        let currentTime = new Date().getTime();
        if (currentTime < activityTime) {
            layer.msg('活动未开始');
        }
        else if (currentTime > activityOverTime) {
            layer.msg('活动已结束');
        }
        else {
            $.when(commonFun.isUserLogin())
                .done(function () {
                    commonFun.useAjax({
                        type:'GET',
                        async: false,
                        url:`/register/user/mobile/1/captcha/1/verify`  //todo
                    },function(response) {
                        if(response.data) {
                            $('.pop_modal_container').show();
                            $('.get_it_btn').hide();
                            $('.got_it_btn').show();
                        }
                        else {
                            ifClickBtn = false;
                        }
                    },function () {
                        ifClickBtn = false;
                    });
                })
                .fail(function () {
                    location.href = '/login';
                });
        }
    }

});

$('.closeBtn').on('click',function () {
    $('.pop_modal_container').hide();
});

$('.see_my_redPocket').on('click',function () {
   location.href = '/m/my-treasure';
});

