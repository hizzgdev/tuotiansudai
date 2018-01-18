require('mWebStyle/account/investor_invest_list.scss');
let tpl = require('art-template/dist/template');
let commonFun = require('publicJs/commonFun');

let menuClick = require('mWebJsModule/menuClick');
let $myInvest = $('#myInvest'),
    $iconMyInvest = $('#iconMyInvest',$myInvest),
    $repayingBtn = $('#repayingBtn',$myInvest),
    $raisingBtn = $('#raisingBtn',$myInvest),
    $investListBox = $('.invest-list-box',$myInvest);

menuClick({
    pageDom: $myInvest
});

$iconMyInvest.on('click',function () {
    location.href='/m'
})
function getList(index,status) {
    commonFun.useAjax({
            url:'/m/investor/invest-list-data',
            type:'get',
            data:{
                index:index,
                status:status
            }
        },
        function (res) {
            console.log('success')
            console.log(res)
            if(res.success == true){
                if(res.data.records.length){
                   $investListBox.html(tpl('investListTpl', res.data));
                }else {
                    if(index == 1){
                        let $noListDOM = $('<div class="noList"><div class="img"></div><button>立即投资</button>');
                        $investListBox.append($noListDOM);
                    }else {
                        $investListBox.append('<p>没有更多数据了</p>');
                    }


                }
            }
        }
    )
}
let index = 1;
getList(1,'REPAYING');
setTimeout(function () {
       let myScroll = new IScroll('#wrapperOut', {
           probeType: 2,
           mouseWheel: true
       });
    myScroll.on('scrollEnd', function () {

        index++;
        //如果滑动到底部，则加载更多数据（距离最底部10px高度）
        if ((this.y - this.maxScrollY) <= 10) {
            if($repayingBtn.hasClass('current')){
                getList(2,'REPAYING')
            }else if($raisingBtn.hasClass('current')){
                getList(2,'RAISING')
            }
        }
    });
},1000)

$repayingBtn.on('click',function () {
    $investListBox.html('');
    getList(1,'REPAYING')
})
$raisingBtn.on('click',function () {
    $investListBox.html('');
    getList(1,'RAISING')
})

$(document).on('click','#toInvest',function () {
    location.href = '/m/loan-list'
})

