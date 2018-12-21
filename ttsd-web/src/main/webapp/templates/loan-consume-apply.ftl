<#import "macro/global.ftl" as global>
<@global.main pageCss="${css.loan_consume_apply}" pageJavascript="${js.loan_consume_apply}" activeNav="我要借款" activeLeftNav="" title="我要借款_抵押借款_拓天速贷" keywords="抵押房产借款,抵押车辆借款,拓天借款,拓天速贷" description="拓天速贷为借款用户提供抵押房产借款和抵押车辆借款服务,拓天借款额度高,门槛低,速度快,利息低,24H放款,借款轻松还.">
<div class="loan_apply_wrapper">
    <h2 class="loan_title">消费借款</h2>
    <input type="hidden" value="${pledgeType.name()}" id="pledgeType" />
    <div class="base_info_wrapper">
        <h3 class="info_title">申请人基础信息</h3>
        <div class="info_wrapper">
            <div>
                <span class="name item">姓名：${userName!}</span>
                <span class="cardId item">身份证：${identityNumber!}</span>
                <span class="address item">地址：${address}</span>
            </div>
            <div>
                <span class="sex item">性别：${sex!}</span>
                <span class="age item">年龄：${age!}</span>
                <span class="tel item">电话：${mobile!}</span>
            </div>
        </div>
    </div>
    <div class="supplement_info_wrapper">
        <h3 class="info_title">请输入补充信息</h3>
        <div class="info_wrapper">
            <div class="supplement_info_item">
                <span class="required-icon">*</span>
                <span class="item_text">婚姻状况：</span>
                <input type="radio" name="marriageState" class="check_radio" id="married" value="MARRIED" style="margin-left: 13px"/><label for="married" class="check_label">已婚</label>
                <input type="radio" name="marriageState" class="check_radio" id="noMarried" value="UNMARRIED"/><label for="noMarried" class="check_label">未婚</label>
                <input type="radio" name="marriageState" class="check_radio" id="divorced" value="DIVORCE"/><label for="divorced" class="check_label">离异</label>
            </div>
            <div class="supplement_info_item">
                <span class="required-icon required-icon-none">*</span>
                <span class="item_text workPosition-text">职位：</span>
                <input type="text" placeholder="例如：技术总监" class="workPosition item-input" maxlength="20"/> 最多20个字
            </div>
            <div class="supplement_info_item">
                <span class="required-icon required-icon-none">*</span>
                <span class="item_text sesameCredit-text">芝麻信用分：</span>
                <input type="text" placeholder="请输入（选填）" class="sesameCredit item-input" maxlength="3"/>
            </div>
        </div>
    </div>
    <div class="loan_application_wrapper">
        <h3 class="info_title">请输入借款申请信息</h3>
        <div class="info_wrapper">
            <div class="loan_application_item">
                <span class="required-icon">*</span>
                <span class="item_text amount_text">借款金额：</span>
                <input type="text" placeholder="请输入（万元）" class="amount item-input" maxlength="4"/>
            </div>
            <div class="loan_application_item clearfix">
                <div style="float: left">
                    <span class="required-icon">*</span>
                    <span class="item_text period_text">借款周期：</span>
                </div>
                <div style="float: left;margin-left: 4px">
                    <span style="display: inline-block;height: 29px;line-height: 29px">每期的时间为30天</span>
                    <div>
                            <input type="radio" name="period" class="check_radio" id="period3" value="3" /><label for="period3" class="check_label">3期（年化8%）</label>
                            <input type="radio" name="period" class="check_radio" id="period6" value="6"/><label for="period6" class="check_label">6期（年化9%）</label>
                            <input type="radio" name="period" class="check_radio" id="period12" value="12"/><label for="period12" class="check_label">12期（年化10%）</label>
                            <input type="radio" name="period" class="check_radio" id="others" value="others"/>
                            <label for="others" class="check_label" style="position: relative;top: 1px;">
                                其他
                                <select name="others" id="othersSelect" >
                                    <option disabled selected value="" style="display: none"></option>
                                    <option value="1">1</option>
                                    <option value="2">2</option>
                                    <option value="3">3</option>
                                    <option value="4">4</option>
                                    <option value="5">5</option>
                                    <option value="6">6</option>
                                    <option value="7">7</option>
                                    <option value="8">8</option>
                                    <option value="9">9</option>
                                    <option value="10">10</option>
                                    <option value="11">11</option>
                                    <option value="12">12</option>
                                </select>

                            </label>
                    </div>
                </div>
            </div>
            <div class="loan_application_item">
                <span class="required-icon">*</span>
                <span class="item_text homeIncome_text">家庭年收入：</span>
                <input type="text" placeholder="请输入（万元）" class="homeIncome item-input" maxlength="4" />
            </div>
            <div class="loan_application_item">
                <span class="required-icon">*</span>
                <span class="item_text loanUsage_text textarea_text">借款用途：</span>
                <textarea type="text" placeholder="请输入（200字以内）" class="loanUsage item-textarea" maxlength="200"></textarea>
            </div>
            <div class="loan_application_item">
                <span class="required-icon">*</span>
                <span class="item_text pledgeInfo_text textarea_text">资产信息：</span>
                <textarea type="text" placeholder="例如房产或者车辆等信息（200字以内）" class="elsePledge item-textarea" maxlength="200"></textarea>
            </div>
        </div>
    </div>
    <div class="evidence">
        <h3 class="info_title">证明材料</h3>
        <div class="info_wrapper">
            <div class="evidence_item">
                <span class="required-icon">*</span>
                <span class="item_text">身份证明：要求拍摄的身份证件照片四角完整，亮度均匀，文字清晰</span>
                <div class="imgWrapper">
                    <div class="img-item positive">
                        <div class="upload-desc">
                            <div class="icon-upload"></div>
                            点击上传人像面
                        </div>
                        <input type="file" name="aaaaa" class="file-input" data-type="positive">
                    </div>
                    <div class="img-item negative">
                        <div class="upload-desc">
                            <div class="icon-upload"></div>
                            点击上传国徽面
                        </div>
                        <input type="file" class="file-input" data-type="negative">
                    </div>
                    <div class="img-item positive_hand">
                        <div class="upload-desc">
                            <div class="icon-upload"></div>
                            点击上传手持身份证人像面
                        </div>
                        <input type="file" class="file-input" data-type="positive_hand">
                    </div>
                    <div class="img-item negative_hand">
                        <div class="upload-desc">
                            <div class="icon-upload"></div>
                            点击上传手持身份证国徽面
                        </div>
                        <input type="file" class="file-input" data-type="negative_hand">
                    </div>
                </div>
            </div>
        </div>
        <div class="info_wrapper">
            <div class="evidence_item">
                <span class="required-icon">*</span>
                <span class="item_text">收入证明：要求上传收入证明或者银行流水等能证明收入的材料</span>
                <div class="imgWrapper">
                    <a class="fancybox" href="http://thyrsi.com/t6/637/1545215955x2890202953.png" rel="example_group">
                        <img class="img" src="http://thyrsi.com/t6/637/1545215955x2890202953.png"/>
                    </a>
                </div>
            </div>
        </div>
    <div class="confirm_btn disabled">确认申请</div>
</div>
</@global.main>