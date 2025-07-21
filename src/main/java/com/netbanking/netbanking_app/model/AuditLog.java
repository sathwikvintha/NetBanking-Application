package com.netbanking.netbanking_app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_ID")
    private Long id;

    @Column(name = "USER_ID")
    private Long targetUserId;

    @Column(name = "ACTION")
    private String actionType;

    @Column(name = "TABLE_NAME")
    private String tableName;

    @Column(name = "RECORD_ID")
    private Long recordId;

    @Column(name = "OLD_VALUES")
    private String oldValues;

    @Column(name = "NEW_VALUES")
    private String newValues;

    @Column(name = "TIMESTAMP")
    private LocalDateTime timestamp;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @Column(name = "USER_AGENT")
    private String userAgent;

    @Column(name = "DETAILS")
    private String description;

    @Column(name = "SEVERITY")
    private String severity;

    @Column(name = "TARGET_USERNAME")
    private String targetUsername;

    @Column(name = "PERFORMED_BY")
    private String performedBy;

    @Column(name = "NOTIFY_USER")
    private Integer notifyUser;

    @Column(name = "NOTIFICATION_MESSAGE")
    private String notificationMessage;

    @Column(name = "DELIVERED")
    private Integer delivered;

    // Getters & Setters
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }

    public String getTargetUsername() { return targetUsername; }
    public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Integer getNotifyUser() { return notifyUser; }
    public void setNotifyUser(Integer notifyUser) { this.notifyUser = notifyUser; }

    public String getNotificationMessage() { return notificationMessage; }
    public void setNotificationMessage(String notificationMessage) { this.notificationMessage = notificationMessage; }

    public Integer getDelivered() { return delivered; }
    public void setDelivered(Integer delivered) { this.delivered = delivered; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }

    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
