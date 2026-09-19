package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDateTime createdAt;

    private Boolean isUnread;

    private String type;

    private String icon;

    public Notification() {}

    public Notification(String id, User user, String title, String description, LocalDateTime createdAt, Boolean isUnread, String type, String icon) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.isUnread = isUnread;
        this.type = type;
        this.icon = icon;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsUnread() { return isUnread; }
    public void setIsUnread(Boolean isUnread) { this.isUnread = isUnread; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public static NotificationBuilder builder() { return new NotificationBuilder(); }

    public static class NotificationBuilder {
        private String id;
        private User user;
        private String title;
        private String description;
        private LocalDateTime createdAt;
        private Boolean isUnread;
        private String type;
        private String icon;

        public NotificationBuilder id(String id) { this.id = id; return this; }
        public NotificationBuilder user(User user) { this.user = user; return this; }
        public NotificationBuilder title(String title) { this.title = title; return this; }
        public NotificationBuilder description(String description) { this.description = description; return this; }
        public NotificationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public NotificationBuilder isUnread(Boolean isUnread) { this.isUnread = isUnread; return this; }
        public NotificationBuilder type(String type) { this.type = type; return this; }
        public NotificationBuilder icon(String icon) { this.icon = icon; return this; }

        public Notification build() {
            return new Notification(id, user, title, description, createdAt, isUnread, type, icon);
        }
    }
}
