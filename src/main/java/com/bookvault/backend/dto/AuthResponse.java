package com.bookvault.backend.dto;

public class AuthResponse {
    private String token;
    private UserDTO user;

    public AuthResponse() {}

    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public static AuthResponseBuilder builder() { return new AuthResponseBuilder(); }

    public static class AuthResponseBuilder {
        private String token;
        private UserDTO user;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder user(UserDTO user) { this.user = user; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, user);
        }
    }
}
