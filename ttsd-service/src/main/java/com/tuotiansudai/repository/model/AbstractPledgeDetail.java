package com.tuotiansudai.repository.model;

import java.io.Serializable;

<<<<<<< HEAD
public abstract class AbstractPledgeDetail implements Serializable{
    private long id;
    private long loanId;
    private String pledgeLocation;
    private String estimateAmount;
    private String loanAmount;
=======
public abstract class AbstractPledgeDetail implements Serializable {
    protected long id;
    protected long loanId;
    protected String pledgeLocation;
    protected String estimateAmount;
    protected String loanAmount;
>>>>>>> features/improve_loan_details_master

    public AbstractPledgeDetail() {
    }

    public AbstractPledgeDetail(long loanId, String pledgeLocation, String estimateAmount, String loanAmount) {
        this.loanId = loanId;
        this.pledgeLocation = pledgeLocation;
        this.estimateAmount = estimateAmount;
        this.loanAmount = loanAmount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public String getPledgeLocation() {
        return pledgeLocation;
    }

    public void setPledgeLocation(String pledgeLocation) {
        this.pledgeLocation = pledgeLocation;
    }

    public String getEstimateAmount() {
        return estimateAmount;
    }

    public void setEstimateAmount(String estimateAmount) {
        this.estimateAmount = estimateAmount;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }
}
