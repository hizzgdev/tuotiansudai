package com.tuotiansudai.fudian.message;

public class BankLoanFullMessage extends BankBaseMessage {

    private long loanId;

    private String loanTxNo;

    private String checkerLoginName;

    private String bankOrderNo;

    private String bankOrderDate;

    public BankLoanFullMessage() {
    }

    public BankLoanFullMessage(boolean status, String message) {
        super(status, message);
    }

    public BankLoanFullMessage(long loanId, String loanTxNo, String checkerLoginName, String bankOrderNo, String bankOrderDate) {
        super(true, null);
        this.loanId = loanId;
        this.loanTxNo = loanTxNo;
        this.checkerLoginName = checkerLoginName;
        this.bankOrderNo = bankOrderNo;
        this.bankOrderDate = bankOrderDate;
    }

    public long getLoanId() {
        return loanId;
    }

    public String getLoanTxNo() {
        return loanTxNo;
    }

    public String getCheckerLoginName() {
        return checkerLoginName;
    }

    public String getBankOrderNo() {
        return bankOrderNo;
    }

    public String getBankOrderDate() {
        return bankOrderDate;
    }
}
