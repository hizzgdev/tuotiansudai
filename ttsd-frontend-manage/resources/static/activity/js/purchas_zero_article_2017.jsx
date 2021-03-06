require("activityStyle/purchas_zero_2017.scss");
require("activityStyle/media.scss");
  let commonFun= require('publicJs/commonFun');
require("publicJs/login_tip");
let dateTime = $('#dateTime');
//商品详情页
let productList = {
    Deerma_humidifier:
    {
        product_name:'德尔玛（Deerma）加湿器 5L大容量',
        market_price:119,
        now_price:0,
        deposit_period:360,
        principal:1600,
        explain:42,
        images:['product/Deerma_humidifier/descrip01.png','product/Deerma_humidifier/descrip02.png',
        'product/Deerma_humidifier/descrip03.png','product/Deerma_humidifier/descrip04.png',
        'product/Deerma_humidifier/descrip05.png','product/Deerma_humidifier/descrip06.png',
    'product/Deerma_humidifier/descrip07.png','product/Deerma_humidifier/descrip08.png',
        'product/Deerma_humidifier/descrip09.png','product/Deerma_humidifier/descrip10.png',
    'product/Deerma_humidifier/descrip11.png','product/Deerma_humidifier/descrip12.png'
    ]


    },
        Trolley_case:
            {
                product_name:'90分商旅两用拉杆箱',
                market_price:349,
                now_price:0,
                deposit_period:360,
                principal:4800,
                explain:127,
                images:['product/Trolley_case/descrip01.png','product/Trolley_case/descrip02.png',
                    'product/Trolley_case/descrip03.png','product/Trolley_case/descrip04.png',
                    'product/Trolley_case/descrip05.png','product/Trolley_case/descrip06.png',
                    'product/Trolley_case/descrip07.png','product/Trolley_case/descrip08.png',
                    'product/Trolley_case/descrip09.png','product/Trolley_case/descrip10.png',
                    'product/Trolley_case/descrip11.png'
                ]


            },
        Philips_Shaver:
            {
                product_name:'飞利浦（PHILIPS）电动剃须刀',
                market_price:649,
                now_price:0,
                deposit_period:360,
                principal:9000,
                explain:238,
                images:['product/Philips_Shaver/descrip01.png','product/Philips_Shaver/descrip02.png',
                    'product/Philips_Shaver/descrip03.png','product/Philips_Shaver/descrip04.png',
                    'product/Philips_Shaver/descrip05.png','product/Philips_Shaver/descrip06.png',
                    'product/Philips_Shaver/descrip07.png'
                ]


            },
        SK_II:
            {
                product_name:'SK-II 神仙水 晶透修护礼盒',
                market_price:1370,
                now_price:0,
                deposit_period:360,
                principal:19000,
                explain:502,
                images:['product/SK_II/descrip01.png','product/SK_II/descrip02.png',
                    'product/SK_II/descrip03.png','product/SK_II/descrip04.png',
                    'product/SK_II/descrip05.png','product/SK_II/descrip06.png',
                    'product/SK_II/descrip07.png','product/SK_II/descrip08.png',
                    'product/SK_II/descrip09.png','product/SK_II/descrip10.png'
                ]


            },
        XiaoMi_5X:
            {
                product_name:'小米5X 4GB+64GB',
                market_price:1499,
                now_price:0,
                deposit_period:360,
                principal:21000,
                explain:555,
                images:['product/XiaoMi_5X/descrip01.png','product/XiaoMi_5X/descrip02.png',
                    'product/XiaoMi_5X/descrip03.png','product/XiaoMi_5X/descrip04.png',
                    'product/XiaoMi_5X/descrip05.png','product/XiaoMi_5X/descrip06.png',
                    'product/XiaoMi_5X/descrip07.png','product/XiaoMi_5X/descrip08.png',
                    'product/XiaoMi_5X/descrip09.png','product/XiaoMi_5X/descrip10.png',
                    'product/XiaoMi_5X/descrip11.png', 'product/XiaoMi_5X/descrip12.png',
                    'product/XiaoMi_5X/descrip13.png', 'product/XiaoMi_5X/descrip14.png'
                ]


            },
        XiaPu_Television:
            {
                product_name:'夏普45英寸智能液晶电视',
                market_price:2099,
                now_price:0,
                deposit_period:360,
                principal:29000,
                explain:766,
                images:['product/XiaPu_Television/descrip01.png','product/XiaPu_Television/descrip02.png',
                    'product/XiaPu_Television/descrip03.png','product/XiaPu_Television/descrip04.png',
                    'product/XiaPu_Television/descrip05.png','product/XiaPu_Television/descrip06.png',
                    'product/XiaPu_Television/descrip07.png','product/XiaPu_Television/descrip08.png',
                    'product/XiaPu_Television/descrip09.png','product/XiaPu_Television/descrip10.png',
                    'product/XiaPu_Television/descrip11.png', 'product/XiaPu_Television/descrip12.png',
                    'product/XiaPu_Television/descrip13.png', 'product/XiaPu_Television/descrip14.png',
                    'product/XiaPu_Television/descrip15.png', 'product/XiaPu_Television/descrip16.png',
                    'product/XiaPu_Television/descrip17.png', 'product/XiaPu_Television/descrip18.png'
                ]


            },
        Philips_Purifier:
            {
                product_name:'飞利浦空气净化器',
                market_price:2799,
                now_price:0,
                deposit_period:360,
                principal:39000,
                explain:1030,
                images:['product/Philips_Purifier/descrip01.png','product/Philips_Purifier/descrip02.png',
                    'product/Philips_Purifier/descrip03.png','product/Philips_Purifier/descrip04.png',
                    'product/Philips_Purifier/descrip05.png','product/Philips_Purifier/descrip06.png',
                    'product/Philips_Purifier/descrip07.png','product/Philips_Purifier/descrip08.png',
                    'product/Philips_Purifier/descrip09.png','product/Philips_Purifier/descrip10.png',
                    'product/Philips_Purifier/descrip11.png', 'product/Philips_Purifier/descrip12.png',
                    'product/Philips_Purifier/descrip13.png', 'product/Philips_Purifier/descrip14.png',
                    'product/Philips_Purifier/descrip15.png', 'product/Philips_Purifier/descrip16.png'
                ]


            },
    Sony_Camera:
            {
                product_name:'索尼DSC-RX100 M3 黑卡相机',
                market_price:3899,
                now_price:0,
                deposit_period:360,
                principal:54000,
                explain:1426,
                images:['product/Sony_Camera/descrip01.png','product/Sony_Camera/descrip02.png',
                    'product/Sony_Camera/descrip03.png','product/Sony_Camera/descrip04.png',
                    'product/Sony_Camera/descrip05.png','product/Sony_Camera/descrip06.png',
                    'product/Sony_Camera/descrip07.png','product/Sony_Camera/descrip08.png',
                    'product/Sony_Camera/descrip09.png','product/Sony_Camera/descrip10.png',
                    'product/Sony_Camera/descrip11.png', 'product/Sony_Camera/descrip12.png',
                    'product/Sony_Camera/descrip13.png', 'product/Sony_Camera/descrip14.png',
                    'product/Sony_Camera/descrip15.png', 'product/Sony_Camera/descrip16.png',
                    'product/Sony_Camera/descrip17.png', 'product/Sony_Camera/descrip18.png'
                ]
            },
    Apple_MacBook:
        {
            product_name:'Apple MacBook Air 13.3英寸笔记本电脑',
            market_price:6588,
            now_price:0,
            deposit_period:360,
            principal:90000,
            explain:2377,
            images:['product/Apple_MacBook/descrip01.png','product/Apple_MacBook/descrip02.png',
                'product/Apple_MacBook/descrip03.png','product/Apple_MacBook/descrip04.png',
                'product/Apple_MacBook/descrip05.png','product/Apple_MacBook/descrip06.png',
                'product/Apple_MacBook/descrip07.png','product/Apple_MacBook/descrip08.png'

            ]


        },
    Iphone_X:
        {
            product_name:'iPhone X 256GB',
            market_price:9688,
            now_price:0,
            deposit_period:360,
            principal:135000,
            explain:3565,
            images:['product/Iphone_X/descrip01.png','product/Iphone_X/descrip02.png',
                'product/Iphone_X/descrip03.png','product/Iphone_X/descrip04.png',
                'product/Iphone_X/descrip05.png','product/Iphone_X/descrip06.png',
                'product/Iphone_X/descrip07.png','product/Iphone_X/descrip08.png',
                'product/Iphone_X/descrip09.png','product/Iphone_X/descrip10.png',
                'product/Iphone_X/descrip11.png','product/Iphone_X/descrip12.png',
                'product/Iphone_X/descrip13.png'

            ]


        }




    }
;
let sourceKind = globalFun.parseURL(location.href);
let product = sourceKind.params.zeroShoppingPrize;

//预览图片
let previewUrl= require('../images/2017/purchas_zero/product/'+product+'/preview.png');
let $previewDOM = $('<img src = "'+previewUrl+'">');
$('#previewImg').html($previewDOM);

//参数说明
$('#itemName').text(productList[product].product_name);//产品名字
$('#marketPrice').text(productList[product].market_price+'元');//市场价
$('#nowPrice').text(productList[product].now_price);//现价
$('#termDay').text(productList[product].deposit_period+'天');//存入期限
$('#principal').text(productList[product].principal+'元');//本金
$('#explain').text('白拿该商品 + 到期收回额外收益（约）'+productList[product].explain+'元');
//图片介绍
$('#productImages').html('');
productList[product].images.forEach(function(item,index) {
    let detailImgUrl= require("../images/2017/purchas_zero/"+item);
    let $detailImgDOM = $('<img src = "'+detailImgUrl+'">');
    $('#productImages').append($detailImgDOM);
})
//去登录
let $soldTipDOM = $('#soldTipDOM');
$('#unLogin').click(function(){
    toLogin();
})
$('.to-close',$soldTipDOM).on('click',function(event) {
    event.preventDefault();
    layer.closeAll();
});


//弹框登录还是链接登录
function toLogin() {
    if (sourceKind.params.source == 'app') {
        location.href = "/login";
    }else {
        layer.open({
            type: 1,
            title: false,
            closeBtn: 0,
            area: ['auto', 'auto'],
            content: $('#loginTip')
        });
    }
}
//日期格式化
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
//不在活动时间范围内时提示信息

let startTime = Number(dateTime.data('starttime').substring(0, 10).replace(/-/gi, '')),
    endTime = Number(dateTime.data('endtime').substring(0, 10).replace(/-/gi, '')),
    currentTime = Number(new Date().Format("yyyyMMdd"));
activityStatus(currentTime,startTime,endTime,dateTime);
$.when(commonFun.isUserLogin())
    .done(function () {
        if(!dateTime.hasClass('activity-ing')) {
            $('.invest-btn').click(function(e){
                e.preventDefault();
                layer.msg('不在活动时间范围内');

            })
        }
    })




//活动状态
function activityStatus(nowDay,startTime,endTime,dom) {
    if (nowDay < startTime) {
        //活动未开始
        dom.removeClass('activity-end');
        dom.removeClass('activity-ing');
        dom.addClass('activity-noStarted');
    }
    else if (nowDay > endTime) {
        //活动已经结束
        dom.removeClass('activity-ing');
        dom.removeClass('activity-noStarted');
        dom.addClass('activity-end');
    }  else if(nowDay>=startTime && nowDay<=endTime){
        //活动中
        dom.removeClass('activity-noStarted');
        dom.removeClass('activity-end');
        dom.addClass('activity-ing');
    }


}

//提示版本更新
let $versionUpdateDOM = $('#versionUpdateDOM');
$versionUpdateDOM.click(function() {
    layer.msg('该活动需更新版本后参加，请升级到最新版本后重试！',{
        time:5000
    });

})

//标的不存在点击弹框显示已售完
if(dateTime.hasClass('activity-ing')) {
    $('#loanNoExist').click(function(){
        layer.open({
            type: 1,
            title: false,
            closeBtn: 0,
            area: ['auto', 'auto'],
            content: $soldTipDOM
        })
    })
}


