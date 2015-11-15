<!DOCTYPE html>
<html>
<#import "macro/global.ftl" as global>
<@global.head title="实名验证" pageCss="${css.register}">
</@global.head>
<body>
<#include "header.ftl" />
<div class="register-container page-width">
    <ul class="step-tab">
        <li class="first"><s></s>1 注册<g></g></li>
        <li class="second on"><s></s>2 实名验证<g></g></li>
        <li class="last"><s></s>3 充值投资<g></g></li>
    </ul>
    <div class="clear-blank"></div>
    <div class="register-box">
        <ul class="reg-list tl register-step-two">
            <form class="register-account-form" action="/register/account" method="post">
                <li>
                    <input type="text" name="userName" placeholder="请输入姓名" class="user-name" value = "${(originalFormData.userName)!}" />
                </li>
                <li>
                    <input type="text" name="identityNumber" placeholder="请输入身份证" class="identity-number" value="${(originalFormData.identityNumber)!}"/>
                </li>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <#if success?? && !success>
                <label class="error">实名认证失败，请检查您提交的信息是否正确</label>
                </#if>
                <input type="submit" class="register-account" value="下一步"/>
            </form>
        </ul>
    </div>
</div>
<#include "footer.ftl">
<@global.javascript pageJavascript="${js.register_account}">
</@global.javascript>
</body>
</html>