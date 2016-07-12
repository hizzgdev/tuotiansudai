package com.tuotiansudai.repository.model;

public class PledgeHouseModel extends AbstractPledgeDetail {
    private String square;
    private String propertyCardId;
    private String estateRegisterId;
    private String authenticAct;

    public PledgeHouseModel() {
        super();
    }

    public PledgeHouseModel(long loanId, String pledgeLocation, String estimateAmount, String pledgeLoanAmount, String square, String propertyCardId, String estateRegisterId, String authenticAct) {
        super(loanId, pledgeLocation, estimateAmount, pledgeLoanAmount);
        this.square = square;
        this.propertyCardId = propertyCardId;
        this.estateRegisterId = estateRegisterId;
        this.authenticAct = authenticAct;
    }

    public String getSquare() {
        return square;
    }

    public void setSquare(String square) {
        this.square = square;
    }

    public String getPropertyCardId() {
        return propertyCardId;
    }

    public void setPropertyCardId(String propertyCardId) {
        this.propertyCardId = propertyCardId;
    }

    public String getEstateRegisterId() {
        return estateRegisterId;
    }

    public void setEstateRegisterId(String estateRegisterId) {
        this.estateRegisterId = estateRegisterId;
    }

    public String getAuthenticAct() {
        return authenticAct;
    }

    public void setAuthenticAct(String authenticAct) {
        this.authenticAct = authenticAct;
    }
}
