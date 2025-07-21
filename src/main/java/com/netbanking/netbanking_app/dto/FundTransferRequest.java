package com.netbanking.netbanking_app.dto;

public class FundTransferRequest {
    private Long fromAccountId;
    private Long fromUserId;
    private String toAccountNumber;
    private Double amount;
    private String remarks;
    private Long userId;

    // Getters and Setters
    public Long getFromAccountId() {
        return fromAccountId;
    }
    public Long getFromUserId() {
        return fromUserId;
    }


    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
