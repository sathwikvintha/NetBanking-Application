package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.model.AuditLog;
import com.netbanking.netbanking_app.repository.AuditLogRepository;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * Full audit logging with user targeting and optional notification delivery.
     */
    @Override
    public void logAction(String actionType,
                          String performedBy,
                          Long targetUserId,
                          String targetUsername,
                          String description,
                          boolean notifyUser,
                          String message) {

        AuditLog log = new AuditLog();
        log.setActionType(actionType);
        log.setPerformedBy(performedBy);
        log.setTargetUserId(targetUserId);
        log.setTargetUsername(targetUsername);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());

        log.setSeverity("INFO");
        log.setTableName("users");
        log.setRecordId(targetUserId);

        log.setIpAddress("N/A");
        log.setUserAgent("N/A");

        log.setNotifyUser(notifyUser ? 1 : 0);
        log.setNotificationMessage(message);

        boolean delivered = notifyUser && notificationService.sendToUser(targetUserId, message);
        log.setDelivered(delivered ? 1 : 0);

        System.out.println("📝 logAction triggered: " + actionType);

        try {
            auditLogRepository.save(log);
            System.out.println("✅ Audit log saved: " + actionType);
        } catch (Exception e) {
            System.out.println("❌ Failed to save audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Simple audit entry for system-level or non-user-specific events.
     */
    @Override
    public void logAction(String actionType) {
        AuditLog log = new AuditLog();
        log.setActionType(actionType);
        log.setSeverity("INFO");
        log.setTimestamp(LocalDateTime.now());
        log.setPerformedBy("SYSTEM"); // Replace with authenticated admin if needed
        log.setDescription("Quick log for action: " + actionType);

        log.setTableName("N/A");
        log.setRecordId(null);
        log.setTargetUserId(null);
        log.setTargetUsername(null);

        log.setIpAddress("N/A");
        log.setUserAgent("N/A");

        log.setNotifyUser(0);
        log.setNotificationMessage(null);
        log.setDelivered(0);

        try {
            auditLogRepository.save(log);
            System.out.println("✅ Quick audit log saved: " + actionType);
        } catch (Exception e) {
            System.out.println("❌ Failed to save quick audit log: " + e.getMessage());
        }
    }

    /**
     * Retrieve all audit logs sorted by most recent.
     */
    @Override
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}
