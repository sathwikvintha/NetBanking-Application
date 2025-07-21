package com.netbanking.netbanking_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "LOANS")
public class LoanApplication {

    @Id
    @SequenceGenerator(name = "loan_seq_gen", sequenceName = "LOAN_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq_gen")
    @Column(name = "LOAN_ID")
    private Long loanId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;

    @Column(name = "LOAN_TYPE", nullable = false)
    private String loanType;

    @Column(name = "LOAN_AMOUNT", nullable = false)
    private Double amount;

    @Column(name = "INTEREST_RATE", nullable = false)
    private Double interestRate;

    @Column(name = "LOAN_DURATION", nullable = false)
    private Integer loanDuration;

    @Column(name = "PURPOSE")
    private String purpose;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "MONTHLY_EMI")
    private BigDecimal monthlyEmi;

    @Column(name = "OUTSTANDING_AMOUNT")
    private BigDecimal outstandingAmount;

    @Column(name = "STATUS", nullable = false)
    private String status = "PENDING";

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "NEXT_DUE_DATE")
    private LocalDate nextDueDate;

    // Optional: expose amount as loanAmount if needed for compatibility
    public Double getLoanAmount() {
        return this.amount;
    }
}
