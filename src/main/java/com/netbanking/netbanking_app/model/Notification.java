package com.netbanking.netbanking_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NOTIFICATIONS")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notif_seq")
    @SequenceGenerator(name = "notif_seq", sequenceName = "ISEQ$$_71393", allocationSize = 1)
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "MESSAGE", nullable = false)
    @Lob
    private String message;

    @Column(name = "NOTIFICATION_TYPE")
    private String notificationType = "INFO";

    @Column(name = "SENT_AT")
    private LocalDateTime sentAt = LocalDateTime.now();

    @Column(name = "IS_READ")
    private String isRead = "N";

    @Column(name = "PRIORITY")
    private String priority = "MEDIUM";

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}
