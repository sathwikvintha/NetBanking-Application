package com.netbanking.netbanking_app.dto;

import java.math.BigDecimal;

public class AdminAccountRequest {

    private Long userId;
    private String accountType;
    private BigDecimal initialBalance;

    private Double interestRate;
    private String branchCode;
    private String ifscCode;
    private String branchName;
    private String status;
    private BigDecimal minBalance;

    public AdminAccountRequest() {
    }

    public AdminAccountRequest(Long userId, String accountType, BigDecimal initialBalance,
                               Double interestRate, String branchCode, String ifscCode,
                               String branchName, String status, BigDecimal minBalance) {
        this.userId = userId;
        this.accountType = accountType;
        this.initialBalance = initialBalance;
        this.interestRate = interestRate;
        this.branchCode = branchCode;
        this.ifscCode = ifscCode;
        this.branchName = branchName;
        this.status = status;
        this.minBalance = minBalance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(BigDecimal minBalance) {
        this.minBalance = minBalance;
    }

    @Override
    public String toString() {
        return "AdminAccountRequest{" +
                "userId=" + userId +
                ", accountType='" + accountType + '\'' +
                ", initialBalance=" + initialBalance +
                ", interestRate=" + interestRate +
                ", branchCode='" + branchCode + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                ", branchName='" + branchName + '\'' +
                ", status='" + status + '\'' +
                ", minBalance=" + minBalance +
                '}';
    }
}
