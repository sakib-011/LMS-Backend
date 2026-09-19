package com.bookvault.backend.dto;

import com.bookvault.backend.entity.Role;

public class UserDTO {
    private String id;
    private String name;
    private String email;
    private Role role;
    private String status;
    private String department;
    private String phone;
    private String avatar;

    public UserDTO() {}

    public UserDTO(String id, String name, String email, Role role, String status, String department, String phone, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.department = department;
        this.phone = phone;
        this.avatar = avatar;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public static UserDTOBuilder builder() { return new UserDTOBuilder(); }

    public static class UserDTOBuilder {
        private String id;
        private String name;
        private String email;
        private Role role;
        private String status;
        private String department;
        private String phone;
        private String avatar;

        public UserDTOBuilder id(String id) { this.id = id; return this; }
        public UserDTOBuilder name(String name) { this.name = name; return this; }
        public UserDTOBuilder email(String email) { this.email = email; return this; }
        public UserDTOBuilder role(Role role) { this.role = role; return this; }
        public UserDTOBuilder status(String status) { this.status = status; return this; }
        public UserDTOBuilder department(String department) { this.department = department; return this; }
        public UserDTOBuilder phone(String phone) { this.phone = phone; return this; }
        public UserDTOBuilder avatar(String avatar) { this.avatar = avatar; return this; }

        public UserDTO build() {
            return new UserDTO(id, name, email, role, status, department, phone, avatar);
        }
    }
}
