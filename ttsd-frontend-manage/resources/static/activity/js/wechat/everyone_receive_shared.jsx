require('activityStyle/wechat/everyone_receive_shared.scss');
let commonFun = require('publicJs/commonFun');
let sourceKind = globalFun.parseURL(location.href);
commonFun.calculationFun(document,window);
let timer;

$('.help_rightNow').on('click',(e) => {
    if(is_wechat()) {

    }
    else {
        alert('请点击浏览器分享');
    }
});

$('.help_too').on('click',() => {
    if(is_wechat()) {
        location.href= '/activity/invite-help/everyone/help/detail';
    }
    else {
        alert('请点击浏览器分享');
    }
});

$('.withdraw_cash').on('click',() => {
    location.href = '/m/account';
});

$('.rules').on('click',() => {
    $('.flex_rules').show();
    $('body').css('overflow','hidden');
});

$('.close_rules').on('click',() => {
    $('.flex_rules').hide();
    $('body').css('overflow','auto');
});

function countTimePop(str) {
    $('.pic_wrapper').show();
    $('.time_over').hide();
    let end = new Date(str).getTime();
    let now = new Date().getTime();
    let leftTime = (end-now)/1000;
    timerCount();
    timer = setInterval(() => {
        timerCount();
    },1000);
    function timerCount() {
        let h,m,s;
        if (leftTime > 0) {
            if (leftTime <= 1) {
                clearInterval(timer);
                $('.pic_wrapper').hide();
                $('.time_over').show();
                return;
            }
            h = Math.floor(leftTime/60/60%24);
            m = Math.floor(leftTime/60%60);
            s = Math.floor(leftTime%60);
            h = h < '10' ? '0' + h : h + '';
            m = m < '10' ? '0' + m : m + '';
            s = s < '10' ? '0' + s : s + '';

            $('.hour1').html(h.charAt(0));
            $('.hour2').html(h.charAt(1));
            $('.minutes1').html(m.charAt(0));
            $('.minutes2').html(m.charAt(1));
            $('.seconds1').html(s.charAt(0));
            $('.seconds2').html(s.charAt(1));
            --leftTime;
        }
        else {
            clearInterval(timer);
            $('.pic_wrapper').hide();
            $('.time_over').show();
        }
    }
}

countTimePop($("#countDown").data("countDown"));

function is_wechat(){
    return navigator.userAgent.toLowerCase().match(/MicroMessenger/i)=="micromessenger";
}

$('.wechat_share_tip').on('click',function () {
    $(this).hide();
});







