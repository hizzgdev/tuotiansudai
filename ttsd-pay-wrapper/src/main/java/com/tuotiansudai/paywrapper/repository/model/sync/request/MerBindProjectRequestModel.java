package com.tuotiansudai.paywrapper.repository.model.sync.request;

import com.google.common.collect.Maps;

import java.util.Map;

public class MerBindProjectRequestModel extends BaseSyncRequestModel{
    private String projectId;
    private String projectName;
    private String projectAmount;
    private String loanUserId;

    public Map<String, String> generatePayRequestData() {
        super.generatePayRequestData();
        Map<String, String> payRequestData = Maps.newHashMap();
        payRequestData.put("service", this.getService());
        payRequestData.put("sign_type", this.getSignType());
        payRequestData.put("charset", this.getCharset());
        payRequestData.put("mer_id", this.getMerId());
        payRequestData.put("version", this.getVersion());
        payRequestData.put("project_id", this.projectId);
        payRequestData.put("project_name", this.projectName);
        payRequestData.put("project_amount", this.projectAmount);
        payRequestData.put("loan_user_id", this.loanUserId);
        return payRequestData;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectAmount() {
        return projectAmount;
    }

    public void setProjectAmount(String projectAmount) {
        this.projectAmount = projectAmount;
    }

    public String getLoanUserId() {
        return loanUserId;
    }

    public void setLoanUserId(String loanUserId) {
        this.loanUserId = loanUserId;
    }
}
