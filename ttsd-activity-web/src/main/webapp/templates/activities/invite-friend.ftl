<#import "../macro/global.ftl" as global>
<@global.main pageCss="${css.invite_friend}" pageJavascript="${js.invite_friend}" activeNav="" activeLeftNav="" title="推荐奖励_拓天速贷" keywords="拓天速贷,推荐奖励,红包奖励,现金奖励,加息劵奖励,推荐机制" description="拓天速贷邀好友拿3重好礼,邀请好友得红包,好友投资送加息劵,还可拿1%现金奖励,拓天速贷可以让您的财富快速升值.">

<div class="tour-slide"></div>

<div class="share-reward-container page-width" id="shareRewardContainer">

    <div class="bg-column-normal invite-line">
        

     <@global.isAnonymous>
        <div class="invite-box-friend anonymous">
            <dl>
                <dt>向好友发送您的邀请链接：</dt>
                <dd>
                    <input type="text" class="input-invite" disabled value="您需要登录才可以邀请好友">
                </dd>
                <dd>
                    <a class="btn-copy-link show-login" href="javascript:void(0);">去登陆</a>
                </dd>
            </dl>
        </div>
      </@global.isAnonymous>

    <@global.isNotAnonymous>

        <@global.noRole hasNoRole="'INVESTOR'">
           <#--已登录未认证-->
            <div class="invite-box-friend anonymous">
                <dl>
                    <dt>向好友发送您的邀请链接：</dt>
                    <dd>
                        <input type="text" class="input-invite"  disabled value="您的好友还不知道您是谁，先来实名认证吧">
                    </dd>
                    <dd>
                        <a class="btn-copy-link to-identification" href="javascript:void(0);" >实名认证</a>
                    </dd>
                </dl>
            </div>
        </@global.noRole>

        <@global.role hasRole="'INVESTOR','LOANER'">
        <#--已登录已认证-->
            <div class="invite-box-friend clearfix non-anonymous yes-identification">
                <div class="invite-list">
                    <i class="icon-line top"><span></span></i>
                    <i class="icon-line bottom"><span></span></i>
                    <h3>邀好友拿<span>3</span>重礼包</h3>
                    <p>您已成功邀请<span>1</span>位好友， 赚取红包<span>100</span>元，赚取现金奖励<span>110</span>元 <a href="">查看邀请详情</a></p>
                    <p class="media-item-phone">
                        <a href="/referrer/refer-list" class="invite-btn">马上邀请好友</a>
                    </p>
                </div>
                <div class="weixin-code">
                    <em class="img-code">
                        <!--[if gte IE 8]>
                        <span style="font-size:12px">请使用更高版本浏览器查看</span>
                        <![endif]-->
                    </em>
                    <span>将扫码后的页面分享给好友即可邀请</span>
                </div>
                <div class="and-text">
                    <span>或</span>
                </div>
                <dl>
                    <dd>
                        <input type="text" class="input-invite" id="clipboard_text1"  readonly data-mobile="<@global.security.authentication property='principal.mobile' />" >
                    </dd>
                    <dt class="clearfix"><a href="javascript:void(0);" class="btn-copy-link fl copy-button" id="copy_btn2"  data-clipboard-action="copy" data-clipboard-target="#clipboard_text1" >复制链接发送给好友</a></dt>
                    <dd>好友通过您发送的链接完成注册即邀请成功</dd>
                </dl>
            </div>

        </@global.role>
    </@global.isNotAnonymous>
    </div>
    <div class="bg-column-normal">
        <div class="titel-item">
            <div class="title-text">
                <i class="left-icon"></i>
                <span>一重礼：好友注册就送礼，人脉即钱脉</span>
                <i class="right-icon"></i>
            </div>
            <div class="tip-item">
                邀请好友在平台完成相应操作即可获得奖励
            </div>
        </div>
        <ul class="info-item media-item">
            <li class="money-type">
                <div class="info-intro">
                    <span class="number-text"><strong>5</strong>元</span>
                    <span>现金红包</span>
                </div>
                <div class="info-name">邀请好友注册即可得</div>
            </li>
            <li class="money-type">
                <div class="info-intro">
                    <span class="number-text"><strong>10</strong>元</span>
                    <span>现金红包</span>
                </div>
                <div class="info-name">好友绑定银行卡可得</div>
            </li>
            <li class="coupon-type">
                <div class="info-intro">
                    <span><strong>+0.5</strong>%</span>
                </div>
                <div class="info-name">好友首次投资抵押类债权再得</div>
            </li>
        </ul>
        <table class="table invite-table">
            <thead>
                <tr>
                    <th>邀好友完成</th>
                    <th>您获得奖励</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>注册</td>
                    <td>5元现金红包</td>
                </tr>
                <tr>
                    <td>绑定银行卡</td>
                    <td>10元现金红包</td>
                </tr>
                <tr>
                    <td>首次投资抵押债权</td>
                    <td>+0.5%加息券</td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="bg-column-normal">
        <div class="titel-item">
            <div class="title-text">
                <i class="left-icon"></i>
                <span>二重礼：一大波红包加码，有情更有益</span>
                <i class="right-icon"></i>
            </div>
        </div>
        <div class="tip-item">
            邀请好友注册投资，每月有效邀请人数越多，红包越多。
        </div>
        <ul class="info-item info-list media-item">
            <li class="money-type">
                <div class="info-intro">
                    <span class="number-text"><strong>18</strong>元</span>
                    <span>现金红包</span>
                </div>
                <div class="info-name">有效邀请2～4人可得</div>
            </li>
            <li class="money-type">
                <div class="info-intro">
                    <span class="number-text"><strong>48</strong>元</span>
                    <span>现金红包</span>
                </div>
                <div class="info-name">有效邀请5～8人可得</div>
            </li>
            <li class="money-type">
                <div class="info-intro">
                    <span class="number-text"><strong>98</strong>元</span>
                    <span>现金红包</span>
                </div>
                <div class="info-name">有效邀请9～10人可得</div>
            </li>
            <li class="money-type">
                <div class="info-intro">
                    <span class="number-text"><strong>288</strong>元</span>
                    <span>现金红包</span>
                </div>
                <div class="info-name">有效邀请10人以上可得</div>
            </li>
        </ul>
        <table class="table invite-table">
            <thead>
                <tr>
                    <th>有效邀请人数</th>
                    <th>您获得奖励</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>2-4人</td>
                    <td>18元现金红包</td>
                </tr>
                <tr>
                    <td>5-8人</td>
                    <td>48元现金红包</td>
                </tr>
                <tr>
                    <td>9-10人</td>
                    <td>98元现金红包</td>
                </tr>
                <tr>
                    <td>10人以上</td>
                    <td>288元现金红包</td>
                </tr>
            </tbody>
        </table>
        <div class="btn-item">
            <p>
                <span>小贴士：</span>好友在注册后15天内投资额达到2000及以上（新手体验项目及债权转让除外），视为一个有效邀请。
            </p>
            <p class="media-item">
                <a href="/referrer/refer-list" class="invite-btn">立即邀请好友拿奖励</a>
            </p>
        </div>
    </div>
    <div class="bg-column-normal">
        <div class="titel-item">
            <div class="title-text">
                <i class="left-icon"></i>
                <span>三重礼：好友投资拿现金，双赢双收益</span>
                <i class="right-icon"></i>
            </div>
        </div>
        <div class="reward-item">
            <img src="${staticServer}/activity/images/invite-friend/reward-img.png" width="100%" class="media-item">
            <img src="${staticServer}/activity/images/invite-friend/reward-img-phone.png" width="100%" class="media-item-phone">
        </div>
        <div class="btn-item media-item">
            <p>
                <a href="/referrer/refer-list" class="invite-btn">立即邀请好友拿奖励</a>
            </p>
        </div>
    </div>
    <div class="bg-column-normal">
        <div class="titel-item">
            <div class="title-text">
                <i class="left-icon"></i>
                <span>好友可享</span>
                <i class="right-icon"></i>
            </div>
        </div>
        <div class="tip-item">
            新手专享高息短期标的：
        </div>
        <div class="loan-item">
            <div class="loan-content">
                <dl>
                    <dt>房产抵押借款<span>新手专享</span></dt>
                    <dd>
                        <span class="bite-text"><strong>13</strong>%</span>
                        <span>预期年化收益</span>
                    </dd>
                    <dd>
                        <span class="bite-text"><strong>30</strong>天</span>
                        <span>项目期限</span>
                    </dd>
                </dl>
            </div>
            <div class="icon-item">
                <span></span>
            </div>
            <ul class="info-item free-list">
                <li class="money-type">
                    <div class="info-intro free-icon">
                        <img src="${staticServer}/activity/images/invite-friend/free-img.png" width="100%">
                    </div>
                    <div class="info-name">体验金6888元</div>
                </li>
                <li class="money-type free-type">
                    <div class="info-intro">
                        <span class="number-text"><strong>688</strong>元</span>
                        <span>现金红包</span>
                    </div>
                    <div class="info-name">现金红包688元</div>
                </li>
                <li class="coupon-type">
                    <div class="info-intro">
                        <span><strong>+3</strong>%</span>
                    </div>
                    <div class="info-name">加息券+3%</div>
                </li>
            </ul>
        </div>
    </div>
    <dl class="activity-rules">
        <dt>温馨提示：</dt>
        <dd>1.您要进行实名认证后才能享受推荐奖励；</dd>
        <dd>2.好友需通过你的专属链接注册才能建立推荐关系；</dd>
        <dd>3.注册奖励将于好友完成指定任务时实时发放；</dd>
        <dd>4.平台将于每月初1-3日统一发放上月的推荐红包奖励，为便于您分散投资，98元及288元红包将根据面额拆分发放，您可以在电脑端“我的账户-我的宝藏”或App端“我的-优惠券”中查看；</dd>
        <dd>5.现金奖励额度为推荐的好友投资本金预期年化收益的1%，奖励计算方法：您的奖励=被推荐人投资金额X（1% / 365 X 标的期限）；</dd>
        <dd>6.现金奖励将于好友投资项目放款后，一次性以现金形式直接发放至您的账户，可以在“我的账户”中查询；</dd>
        <dd>7.活动中如发现恶意注册虚假账号、恶意刷奖等违规操作及作弊行为，若判定为违规操作及作弊行为，拓天速贷将取消您的获奖的资格，并有权撤销违规交易；</dd>
        <dd>8.活动遵守拓天速贷法律声明，最终解释权归拓天速贷平台所有。</dd>
    </dl>
</div>

<div class="pop-layer-out" style="display: none">
    <div class="btn-to-close"></div>
    <p>您的好友可能猜不到你是谁<br />先来进行实名认证吧！</p>

    <a href="/register/account?redirect=/activity/share-reward"  class="btn-to-identification"></a>
</div>

<#include "login-tip.ftl" />
</@global.main>