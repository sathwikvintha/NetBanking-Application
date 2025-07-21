package com.netbanking.netbanking_app.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    // 🔍 Direct reference for logging/filtering
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    // 👤 Relationship for admin views
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "loan_amount", nullable = false)
    private BigDecimal loanAmount;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "loan_duration")
    private Integer loanDuration;

    @Column(name = "loan_type")
    private String loanType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "monthly_emi")
    private Double monthlyEmi;

    @Column(name = "outstanding_amount")
    private Double outstandingAmount;

    @Column(name = "purpose")
    private String purpose;

    // ✅ Default to "PENDING" unless set explicitly
    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "updated_at")
    private LocalDate updatedAt = LocalDate.now();
}
