package com.netbanking.netbanking_app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACCOUNTS")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "ACCOUNT_NUMBER", nullable = false, unique = true)
    private String accountNumber;


    @Column(name = "ACCOUNT_TYPE", nullable = false)
    private String accountType;

    @Column(name = "BALANCE", nullable = false)
    private BigDecimal balance;

    @Column(name = "INTEREST_RATE")
    private Double interestRate;

    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "IFSC_CODE")
    private String ifscCode;

    @Column(name = "BRANCH_NAME")
    private String branchName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "MINIMUM_BALANCE")
    private BigDecimal minBalance;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;


    // Default constructor
    public Account() {
    }

    // Constructor with parameters
    public Account(Long userId, String accountNumber, String accountType, BigDecimal balance) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }

    // ✅ Getters & Setters
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return accountId != null ? accountId.hashCode() : 0;
    }

    public void setUpdatedAt(LocalDateTime now) {
    }

    public String getStatus() {
        return status;
    }

    public String getBranchName() {
        return branchName;
    }


    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMinBalance(BigDecimal minBalance) {
        this.minBalance = minBalance;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}