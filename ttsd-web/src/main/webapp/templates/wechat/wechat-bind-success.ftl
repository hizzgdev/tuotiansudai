<#import "wechat-global.ftl" as global>
<@global.main pageCss="${css.wechat_entry_point}" pageJavascript="${js.wechat_bind_success}">
<div class="weChat-container success-box">
    <span class="result-ok">
        <i class="icon-sucess"></i>
        <em>登录成功</em>
    </span>

    <span class="down-time"><i id="downTime" data-redirect="${redirect!('/')}"></i>后自动跳转</span>
    <button type="button" class="btn-normal">确认</button>
</div>
</@global.main>