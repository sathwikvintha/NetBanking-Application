package com.netbanking.netbanking_app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BILLS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_seq")
    @SequenceGenerator(name = "bill_seq", sequenceName = "ISEQ$$_71410", allocationSize = 1)
    @Column(name = "BILL_ID")
    private Long billId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "BILLER_NAME", nullable = false)
    private String billerName;

    @Column(name = "BILL_TYPE", nullable = false)
    private String billType;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDate dueDate;

    @Column(name = "BILL_NUMBER")
    private String billNumber;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "STATUS")
    private String status = "UNPAID";

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;

    @Column(name = "LATE_FEE")
    private Double lateFee = 0.0;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
