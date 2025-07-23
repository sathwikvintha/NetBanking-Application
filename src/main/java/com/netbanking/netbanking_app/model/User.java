package com.netbanking.netbanking_app.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "USER_SEQ", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;


    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "PHONE", nullable = false)
    private String phone;

    @Transient
    private List<Account> accounts;
    @Transient
    private List<Card> cards;
    @Transient
    private List<Loan> loans;
    @Transient
    private List<Bill> bills;




    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    @Column(name = "KYC_STATUS")
    private String kycStatus;

    @Column(name = "DOB")
    private LocalDate dob; // or java.sql.Date depending on your stack

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "CITY")
    private String city;

    @Column(name = "REGISTRATION_DATE")
    private LocalDate registrationDate;

    @Column(name = "PARENT_NUM")
    private String parentNum;

    private LocalDateTime updatedAt;


    // ===== Getters and Setters =====

    public Long getUserId() { return userId; }

    public LocalDate getDob() {
        return dob;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCity() {
        return city;
    }


    public String getParentNum() {
        return parentNum;
    }

    public void setParentNum(String parentNum) {
        this.parentNum = parentNum;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }


    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public String getIsActive() { return isActive; }

    public void setIsActive(String isActive) { this.isActive = isActive; }

    public String getKycStatus() { return kycStatus; }

    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }

    // ✅ Add setters & getters
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    public List<Account> getAccounts() {
        return accounts;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
    public List<Card> getCards() {
        return cards;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }
    public List<Loan> getLoans() {
        return loans;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }
}
