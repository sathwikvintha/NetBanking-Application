package com.netbanking.netbanking_app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CARDS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq")
    @SequenceGenerator(
            name = "card_seq",
            sequenceName = "CARD_SEQUENCE", // Ensure this sequence exists in Oracle
            allocationSize = 1
    )
    @Column(name = "CARD_ID")
    private Long cardId;

    // 👉 Read-only mapping for direct userId access
    @Column(name = "USER_ID", insertable = false, updatable = false)
    private Long userId;

    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;

    @Column(name = "CARD_NUMBER", nullable = false, unique = true)
    private String cardNumber;

    @Column(name = "CARD_TYPE", nullable = false)
    private String cardType;

    @Column(name = "CARDHOLDER_NAME", nullable = false)
    private String cardholderName;

    @Column(name = "CVV", nullable = false)
    private String cvv;

    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "ISSUE_DATE", nullable = false)
    private LocalDate issueDate = LocalDate.now();

    @Column(name = "DAILY_LIMIT", nullable = false)
    private Double dailyLimit = 50000.00;

    @Column(name = "STATUS", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "PIN_HASH")
    private String pinHash;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ✅ Proper relationship mapping
    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
    private User user;

    public String getCardType() {
        return cardType;
    }
}
