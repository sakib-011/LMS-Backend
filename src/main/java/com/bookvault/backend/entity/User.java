package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String status;

    private String department;

    private String phone;

    private String avatar;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    public User() {}

    public User(String id, String name, String email, String password, Role role, String status, String department, String phone, String avatar, LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.department = department;
        this.phone = phone;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    @PrePersist
    protected void onCreate() {
        if (id == null || id.isEmpty()) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "Active";
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public static UserBuilder builder() { return new UserBuilder(); }

    public static class UserBuilder {
        private String id;
        private String name;
        private String email;
        private String password;
        private Role role;
        private String status;
        private String department;
        private String phone;
        private String avatar;
        private LocalDateTime createdAt;
        private LocalDateTime lastLogin;

        public UserBuilder id(String id) { this.id = id; return this; }
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder role(Role role) { this.role = role; return this; }
        public UserBuilder status(String status) { this.status = status; return this; }
        public UserBuilder department(String department) { this.department = department; return this; }
        public UserBuilder phone(String phone) { this.phone = phone; return this; }
        public UserBuilder avatar(String avatar) { this.avatar = avatar; return this; }
        public UserBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserBuilder lastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; return this; }

        public User build() {
            return new User(id, name, email, password, role, status, department, phone, avatar, createdAt, lastLogin);
        }
    }
}
