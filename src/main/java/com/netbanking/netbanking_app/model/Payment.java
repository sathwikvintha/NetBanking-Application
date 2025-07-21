package com.netbanking.netbanking_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PAYMENTS")
public class Payment {

    @Id
    @SequenceGenerator(name = "payment_seq_gen", sequenceName = "PAYMENT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq_gen")
    @Column(name = "PAYMENT_ID")
    private Long paymentId;

    @Column(name = "BILL_ID", nullable = false)
    private Long billId;

    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "TRANSACTION_FEE")
    private BigDecimal transactionFee;

    @Column(name = "CONFIRMATION_NUMBER")
    private String confirmationNumber;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    public void setUpdatedAt(LocalDateTime now) {
    }
}
