package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.model.AuditLog;

import java.util.List;

public interface AuditLogService {

    /**
     * Log a new admin action, optionally sending a notification to the user.
     *
     * @param actionType The type of action performed (e.g., "KYC_VERIFIED", "USER_CREATED")
     * @param performedBy Username or ID of the admin who performed the action
     * @param targetUserId The affected user's ID
     * @param targetUsername The affected user's username
     * @param description A descriptive summary of what was done
     * @param notifyUser Whether a notification should be sent to the user
     * @param message Notification message content (if notifyUser is true)
     */
    void logAction(String actionType,
                   String performedBy,
                   Long targetUserId,
                   String targetUsername,
                   String description,
                   boolean notifyUser,
                   String message);

    List<AuditLog> getAllLogs();

    void logAction(String adminOtpValidated);
}
