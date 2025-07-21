package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.model.Notification;

import java.util.List;

public interface NotificationService {
    Notification sendNotification(Long userId, String message, String type, String priority);
    List<Notification> getUserNotifications(Long userId);

    void triggerSqlNotification(Long userId, String message);

    // 🔔 Added for audit logging context
    boolean sendToUser(Long userId, String message);

    void sendEmail(String toEmail, String subject, String body);

}
