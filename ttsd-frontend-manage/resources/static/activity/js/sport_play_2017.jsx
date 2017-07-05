require("activityStyle/sport_play_2017.scss");
let commonFun= require('publicJs/commonFun');
require('publicJs/login_tip');
let drawCircle = require('activityJsModule/gift_circle_draw');
let tpl = require('art-template/dist/template');



let $sportPlayContainer = $('#sportPlayContainer'),
    tipGroupObj = {},
    sourceKind = globalFun.parseURL(location.href);
let topimg=require('../images/2017/sport-play/top-img.jpg'),
    topimgPhone=require('../images/2017/sport-play/top-img-phone.jpg');

$sportPlayContainer.find('.top-img .media-pc').attr('src',topimg).siblings('.media-phone').attr('src',topimgPhone);

let drawBtn=require('../images/2017/sport-play/draw-btn.png');
$sportPlayContainer.find('.draw-model img').attr('src',drawBtn);


$sportPlayContainer.find('.tip-list-frame .tip-list').each(function (key, option) {
    let kind = $(option).data('return');
    tipGroupObj[kind] = option;
});

let $pointerImg = $('.draw-btn', $sportPlayContainer);


$pointerImg.on('click', function () {
    //未登录
    if (sourceKind.params.source == 'app') {
        $.when(commonFun.isUserLogin())
            .done(function () {
                getGift();
            })
            .fail(function () {
                location.href = "/login";
            });
    } else {
        getGift();
    }
});

function getGift() {
    //判断是否正在抽奖
    if ($pointerImg.hasClass('lottering')) {
        return;//不能重复抽奖
    }
    $pointerImg.addClass('lottering');
    //延迟1秒抽奖
    setTimeout(function () {
        $.ajax({
            url: '/activity/exercise-work/exercise-work-draw?activityCategory=EXERCISE_WORK_ACTIVITY',
            type: 'GET',
            // dataType: 'json',
            // data: {
            //     'activityCategory':'EXERCISE_WORK_ACTIVITY'
            // }
        })
        .done(function(data) {
            console.log(data);
            $pointerImg.removeClass('lottering');
            // if (data.returnCode == 0) {
            //     var prizeType = data.prizeType.toLowerCase();
            //     $(tipGroupObj[prizeType]).find('.prizeValue').text(data.prizeValue);
            //     drawCircle.noRotateFn(tipGroupObj[prizeType]);

            // } else if (data.returnCode == 1) {
            //     //没有抽奖机会
            //     drawCircle.tipWindowPop(tipGroupObj['nochance']);
            // }
            // else if (data.returnCode == 2) {
            //     //判断是否需要弹框登陆
            //     layer.open({
            //         type: 1,
            //         title: false,
            //         closeBtn: 0,
            //         area: ['auto', 'auto'],
            //         content: $('#loginTip')
            //     });  //弹框登录
            // } else if (data.returnCode == 3) {
            //     //不在活动时间范围内！
            //     drawCircle.tipWindowPop(tipGroupObj['expired']);

            // } else if (data.returnCode == 4) {
            //     //实名认证
            //     drawCircle.tipWindowPop(tipGroupObj['authentication']);
            // }
        })
        .fail(function() {
            $pointerImg.removeClass('lottering');
            layer.msg('请求失败，请重试！');
        });
    }, 1000);
}


// 选择礼品
$sportPlayContainer.find('.gift-list .select-item').on('click',  function(event) {
    event.preventDefault();
    let $self=$(this);
    if(!$self.hasClass('active')){
        $self.addClass('active').siblings('.select-item').removeClass('active')
        .closest('.gift-item').siblings().find('.select-item').removeClass('active');
    }
});

// 兑换商品
$sportPlayContainer.find('.gift-item .text-item').on('click',  function(event) {
    event.preventDefault();
    let $self=$(this),
        isSelect=$self.closest('.gift-item').find('.select-item').hasClass('active');
    
    if(isSelect){
        $.ajax({
            url: '/activity/exercise-work/exchange-prize',
            type: 'POST',
            dataType: 'json',
            data: {
                'exchangePrize': 'value1'
            }
        })
        .done(function(data) {
            console.log(data);
        })
        .fail(function() {
            layer.msg('请求失败，请重试！');
        });
    }else{
        layer.msg('请选择要兑换的物品！');
    }
});
