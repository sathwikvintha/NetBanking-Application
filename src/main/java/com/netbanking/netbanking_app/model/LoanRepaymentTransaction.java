package com.netbanking.netbanking_app.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "LOAN_REPAYMENT_TRANSACTION")
public class LoanRepaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_repay_seq_gen")
    @SequenceGenerator(name = "loan_repay_seq_gen", sequenceName = "LOAN_REPAYMENT_SEQ", allocationSize = 1)
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;

    @Column(name = "LOAN_ID", nullable = false)
    private Long loanId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "AMOUNT_PAID", nullable = false)
    private BigDecimal amountPaid;

    @Column(name = "EMI_NUMBER", nullable = false)
    private Integer emiNumber;

    @Column(name = "TOTAL_EMIS", nullable = false)
    private Integer totalEmis;

    @Column(name = "PAYMENT_DATE")
    private LocalDate paymentDate;

    @Column(name = "PAYMENT_MODE")
    private String paymentMode;

    @Column(name = "STATUS")
    private String status = "SUCCESS";

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters and setters
}
