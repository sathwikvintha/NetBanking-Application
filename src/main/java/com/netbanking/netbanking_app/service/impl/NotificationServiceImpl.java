package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.model.Notification;
import com.netbanking.netbanking_app.repository.NotificationRepository;
import com.netbanking.netbanking_app.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public Notification sendNotification(Long userId, String message, String type, String priority) {
        if (userId == null || message == null || message.trim().isEmpty()) {
            System.out.println("❌ Invalid notification data");
            return null;
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setNotificationType(type != null ? type : "INFO");
        notification.setPriority(priority != null ? priority : "MEDIUM");
        notification.setIsRead("N");
        notification.setSentAt(LocalDateTime.now());
        notification.setCreatedAt(LocalDateTime.now());

        try {
            Notification saved = notificationRepository.save(notification);
            System.out.println("✅ Notification saved → ID = " + saved.getNotificationId());
            return saved;
        } catch (Exception e) {
            System.out.println("🚨 Failed to save notification: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }


    @Override
    public boolean sendToUser(Long userId, String message) {
        if (userId == null || message == null || message.trim().isEmpty()) {
            System.out.println("⚠️ Skipping notification: userId or message is null/empty");
            return false;
        }

        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setMessage(message.trim());
            notification.setNotificationType("INFO");
            notification.setPriority("LOW");
            notification.setIsRead("N");
            notification.setSentAt(LocalDateTime.now());
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);

            System.out.printf("📨 Audit notification saved for userId=%d [%s]%n", userId, message);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to send audit notification to userId=" + userId);
            e.printStackTrace(); // Optional for deeper debugging
            return false;
        }
    }




    @Override
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void triggerSqlNotification(Long userId, String message) {
        try {
            notificationRepository.triggerNotification(userId, message);
            System.out.println("✅ Triggered SQL notification for userId=" + userId);
        } catch (Exception e) {
            System.out.println("❌ Failed to trigger SQL notification: " + e.getMessage());
            throw e;
        }
    }

}
