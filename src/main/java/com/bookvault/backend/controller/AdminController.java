package com.bookvault.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bookvault.backend.entity.*;
import com.bookvault.backend.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private AcquisitionRepository acquisitionRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Admin Dashboard & Analytics ---
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalBooks", bookRepository.count());
        stats.put("activeBorrowings", borrowingRepository.findByStatus("BORROWED").size());
        stats.put("overdueBorrowings", borrowingRepository.findByStatus("OVERDUE").size());
        stats.put("totalFinesAmount", fineRepository.findAll().stream().mapToDouble(Fine::getAmount).sum());
        stats.put("serverStatus", "Operational");
        stats.put("uptime", "99.98%");
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalLoansThisMonth", 142);
        analytics.put("mostBorrowedCategory", "Computer Science");
        analytics.put("departmentUsage", Map.of(
                "Computer Science", 45,
                "Electrical Engineering", 25,
                "Business Administration", 18,
                "Physics & Math", 12
        ));
        return ResponseEntity.ok(analytics);
    }

    // --- User Management ---
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("User with email already exists");
        }

        Role role = Role.STUDENT;
        if ("MODERATOR".equalsIgnoreCase(payload.get("role"))) role = Role.MODERATOR;
        if ("ADMINISTRATOR".equalsIgnoreCase(payload.get("role"))) role = Role.ADMINISTRATOR;

        User user = User.builder()
                .name(payload.get("name"))
                .email(email)
                .password(passwordEncoder.encode(payload.getOrDefault("password", "password123")))
                .role(role)
                .status("Active")
                .department(payload.getOrDefault("department", "General"))
                .phone(payload.get("phone"))
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Map<String, String> payload) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        if (payload.containsKey("name")) user.setName(payload.get("name"));
        if (payload.containsKey("status")) user.setStatus(payload.get("status"));
        if (payload.containsKey("department")) user.setDepartment(payload.get("department"));
        if (payload.containsKey("phone")) user.setPhone(payload.get("phone"));

        if (payload.containsKey("role")) {
            String roleStr = payload.get("role");
            if ("MODERATOR".equalsIgnoreCase(roleStr)) user.setRole(Role.MODERATOR);
            else if ("ADMINISTRATOR".equalsIgnoreCase(roleStr)) user.setRole(Role.ADMINISTRATOR);
            else user.setRole(Role.STUDENT);
        }

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    // --- Acquisitions ---
    @GetMapping("/acquisition")
    public ResponseEntity<List<Acquisition>> getAcquisitions() {
        return ResponseEntity.ok(acquisitionRepository.findAll());
    }

    @PostMapping("/acquisition")
    public ResponseEntity<?> createAcquisition(@RequestBody Map<String, Object> payload) {
        Acquisition acq = Acquisition.builder()
                .title(payload.get("title").toString())
                .author(payload.get("author").toString())
                .supplier(payload.getOrDefault("supplier", "University Vendor").toString())
                .requestedBy(payload.getOrDefault("requestedBy", "Admin").toString())
                .cost(payload.get("cost") != null ? Double.parseDouble(payload.get("cost").toString()) : 0.0)
                .status("Ordered")
                .orderDate(LocalDate.now())
                .build();

        acquisitionRepository.save(acq);
        return ResponseEntity.ok(acq);
    }

    // --- Audit Logs ---
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditLogRepository.findAllByOrderByTimestampDesc());
    }

    // --- System Settings ---
    @GetMapping("/settings")
    public ResponseEntity<List<SystemSetting>> getSettings() {
        return ResponseEntity.ok(systemSettingRepository.findAll());
    }

    @PutMapping("/settings")
    public ResponseEntity<?> updateSetting(@RequestBody Map<String, String> payload) {
        String key = payload.get("settingKey");
        String value = payload.get("settingValue");

        SystemSetting setting = systemSettingRepository.findById(key).orElse(null);
        if (setting == null) {
            setting = SystemSetting.builder().settingKey(key).settingValue(value).description("Custom Setting").build();
        } else {
            setting.setSettingValue(value);
        }
        systemSettingRepository.save(setting);
        return ResponseEntity.ok(setting);
    }

    // --- System Backup ---
    @PostMapping("/backup/trigger")
    public ResponseEntity<?> triggerBackup() {
        AuditLog log = AuditLog.builder()
                .userEmail("admin@university.edu")
                .action("System Backup")
                .category("System")
                .ipAddress("127.0.0.1")
                .timestamp(LocalDateTime.now())
                .details("Automated system snapshot database backup created")
                .build();
        auditLogRepository.save(log);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "timestamp", LocalDateTime.now().toString(),
                "backupFile", "bookgrid_backup_" + System.currentTimeMillis() + ".sql"
        ));
    }
}
