<#import "../../macro/global.ftl" as global>

<@global.main pageCss="${css.purchas_zero_2017}" pageJavascript="${js.purchas_zero_article_2017}" activeNav="" activeLeftNav="" title="好货0元购_活动中心_拓天速贷" keywords="拓天速贷,0元购,商品奖励,专享项目" description="拓天速贷好货0元购活动,活动期间用户点选心仪好货,通过活动页面'立即投资'按钮进入投资页面,投资带有'0元购'标签的专享项目达到该商品对应投资额,即可0元白拿商品.">

<div id="purchas_zero_detail" class="shopping-zero-frame">
   <div class="product-intro-wrap clearfix page-width page680">
            <div class="preview-wrap" id="previewImg">
            </div>
       <div class="itemInfo-wrap">
           <h2 class="item-name" id="itemName"></h2>
           <p class="info-price">
               市场价：<del id="marketPrice"></del> <strong id="nowPrice"></strong> 元
           </p>
           <ul>
               <li>存入期限： <span id="termDay"></span> </li>
               <li>存入本金：<span id="principal"></span></li>
               <li class="media-320">收益说明：<span id="explain"></span></li>
           </ul>
           <a href="javascript:;" class="invest-btn" id="toInvest">立即白拿</a>
           <a href="javascript:;" class="invest-btn" id="loanNoExist">立即白拿</a>
       </div>
   </div>
    <div class="shopping-process page-width">
        <h2></h2>
    </div>
    <div class="product-detail page-width">
        <h2 class="detail-product">商品介绍</h2>
        <div class="product-imgs" id="productImages">
        </div>
    </div>
    <div id="soldTipDOM" class="sold-tip" style="display: none">
        <div class="close-btn"></div>
        <div class="icon-sold"></div>
        <p>太火爆了！该标的已售罄，<br/>
            新标的正在努力筹备中，请稍候重试...</p>
    </div>
    <#include "../../module/login-tip.ftl" />
</div>
</@global.main>