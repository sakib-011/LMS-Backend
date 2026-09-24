package com.bookvault.backend.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bookvault.backend.dto.AuthResponse;
import com.bookvault.backend.dto.LoginRequest;
import com.bookvault.backend.dto.RegisterRequest;
import com.bookvault.backend.dto.UserDTO;
import com.bookvault.backend.entity.Role;
import com.bookvault.backend.entity.User;
import com.bookvault.backend.repository.UserRepository;
import com.bookvault.backend.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private com.bookvault.backend.service.EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .studentId(user.getStudentId() != null ? user.getStudentId() : user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .department(user.getDepartment())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .build();

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        Role userRole = registerRequest.getRole() != null ? registerRequest.getRole() : Role.STUDENT;

        String studentIdToUse = (registerRequest.getStudentId() != null && !registerRequest.getStudentId().trim().isEmpty())
                ? registerRequest.getStudentId().trim()
                : "STU-" + System.currentTimeMillis();

        User user = User.builder()
                .id(studentIdToUse)
                .studentId(studentIdToUse)
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(userRole)
                .status("Active")
                .department(registerRequest.getDepartment() != null ? registerRequest.getDepartment() : "General")
                .phone(registerRequest.getPhone())
                .build();

        userRepository.save(user);

        String token = tokenProvider.generateTokenForEmail(user.getEmail());

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), user.getName(), token);

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .studentId(user.getStudentId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .department(user.getDepartment())
                .phone(user.getPhone())
                .build();

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build());
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody java.util.Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("success", false, "message", "Email is required"));
        }

        User user = userRepository.findByEmail(email).orElse(null);
        String name = user != null ? user.getName() : "Student";
        String token = tokenProvider.generateTokenForEmail(email);

        boolean sent = emailService.sendVerificationEmail(email, name, token);

        return ResponseEntity.ok(java.util.Map.of(
                "success", sent,
                "message", sent ? "Verification email sent successfully to " + email : "Failed to send email. Check SMTP settings."
        ));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody java.util.Map<String, String> payload) {
        String token = payload.get("token");
        String email = payload.get("email");

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("success", false, "message", "Token is required"));
        }

        if (!tokenProvider.validateToken(token)) {
            return ResponseEntity.badRequest().body(java.util.Map.of("success", false, "message", "Invalid or expired verification token"));
        }

        String tokenEmail = tokenProvider.getEmailFromJWT(token);
        String targetEmail = (email != null && !email.trim().isEmpty()) ? email : tokenEmail;

        User user = userRepository.findByEmail(targetEmail).orElse(null);
        if (user != null) {
            user.setStatus("Active");
            userRepository.save(user);
        }

        return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "Email verified successfully! Your account is now active.",
                "email", targetEmail
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .department(user.getDepartment())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .build();

        return ResponseEntity.ok(userDTO);
    }
}

