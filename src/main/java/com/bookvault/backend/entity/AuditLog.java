package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userEmail;

    private String action;

    private String category;

    private String ipAddress;

    private LocalDateTime timestamp;

    @Column(length = 1500)
    private String details;

    public AuditLog() {}

    public AuditLog(String id, String userEmail, String action, String category, String ipAddress, LocalDateTime timestamp, String details) {
        this.id = id;
        this.userEmail = userEmail;
        this.action = action;
        this.category = category;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public static AuditLogBuilder builder() { return new AuditLogBuilder(); }

    public static class AuditLogBuilder {
        private String id;
        private String userEmail;
        private String action;
        private String category;
        private String ipAddress;
        private LocalDateTime timestamp;
        private String details;

        public AuditLogBuilder id(String id) { this.id = id; return this; }
        public AuditLogBuilder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
        public AuditLogBuilder action(String action) { this.action = action; return this; }
        public AuditLogBuilder category(String category) { this.category = category; return this; }
        public AuditLogBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public AuditLogBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public AuditLogBuilder details(String details) { this.details = details; return this; }

        public AuditLog build() {
            return new AuditLog(id, userEmail, action, category, ipAddress, timestamp, details);
        }
    }
}
