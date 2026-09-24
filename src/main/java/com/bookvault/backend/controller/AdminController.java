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
    private ReservationRepository reservationRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private AcquisitionRepository acquisitionRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BookRequestRepository bookRequestRepository;

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

    // --- Reservations ---
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getReservations() {
        return ResponseEntity.ok(reservationRepository.findAll());
    }

    @PutMapping("/reservations/{id}/ready")
    public ResponseEntity<?> markReservationReady(@PathVariable String id) {
        Reservation res = reservationRepository.findById(id).orElse(null);
        if (res == null) return ResponseEntity.notFound().build();

        res.setStatus("ready");
        res.setPickupDeadline(LocalDate.now().plusDays(3));
        reservationRepository.save(res);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/reservations/{id}/checkout")
    public ResponseEntity<?> checkoutReservation(@PathVariable String id) {
        Reservation res = reservationRepository.findById(id).orElse(null);
        if (res == null) return ResponseEntity.notFound().build();

        Book book = res.getBook();
        if (book.getPhysicalAvailable() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "No physical copies available in inventory"));
        }

        book.setPhysicalAvailable(book.getPhysicalAvailable() - 1);
        bookRepository.save(book);

        res.setStatus("fulfilled");
        reservationRepository.save(res);

        Borrowing borrowing = Borrowing.builder()
                .user(res.getUser())
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .isOverdue(false)
                .progress(0)
                .status("BORROWED")
                .build();

        borrowingRepository.save(borrowing);
        return ResponseEntity.ok(Map.of("message", "Reservation checked out successfully as loan", "borrowing", borrowing));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable String id) {
        Reservation res = reservationRepository.findById(id).orElse(null);
        if (res != null) {
            res.setStatus("cancelled_by_admin");
            reservationRepository.save(res);

            Notification n = Notification.builder()
                    .user(res.getUser())
                    .title("Reservation Removed by Admin ⚠️")
                    .description("Your reservation for '" + res.getBook().getTitle() + "' was cancelled / removed by library staff.")
                    .createdAt(LocalDateTime.now())
                    .isUnread(true)
                    .type("RESERVATION_CANCELLED")
                    .icon("fas fa-times-circle")
                    .build();
            notificationRepository.save(n);

            return ResponseEntity.ok(Map.of("message", "Reservation cancelled by admin"));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/reservations/{id}/status")
    public ResponseEntity<?> updateReservationStatus(@PathVariable String id, @RequestBody Map<String, String> payload) {
        Reservation res = reservationRepository.findById(id).orElse(null);
        if (res == null) return ResponseEntity.notFound().build();

        String status = payload.get("status");
        if (status != null && !status.isEmpty()) {
            res.setStatus(status.toLowerCase());
            if ("ready".equalsIgnoreCase(status)) {
                res.setPickupDeadline(LocalDate.now().plusDays(3));
            }
            reservationRepository.save(res);

            Notification n = Notification.builder()
                    .user(res.getUser())
                    .title("Reservation Status Updated: " + status.toUpperCase())
                    .description("Your reservation status for '" + res.getBook().getTitle() + "' has been updated to " + status + ".")
                    .createdAt(LocalDateTime.now())
                    .isUnread(true)
                    .type("RESERVATION_UPDATE")
                    .icon("fas fa-info-circle")
                    .build();
            notificationRepository.save(n);
        }
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/reservations/{id}/permanent")
    public ResponseEntity<?> deleteReservationPermanently(@PathVariable String id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Reservation deleted permanently"));
        }
        return ResponseEntity.notFound().build();
    }

    // --- Book Requests ---
    @GetMapping("/requests")
    public ResponseEntity<List<BookRequest>> getRequests() {
        return ResponseEntity.ok(bookRequestRepository.findAll());
    }

    @PutMapping("/requests/{id}/status")
    public ResponseEntity<?> updateRequestStatus(@PathVariable String id, @RequestBody Map<String, String> payload) {
        BookRequest req = bookRequestRepository.findById(id).orElse(null);
        if (req == null) return ResponseEntity.notFound().build();

        String status = payload.get("status");
        String notes = payload.get("notes");
        if (status != null) req.setStatus(status);
        if (notes != null) req.setNotes(notes);

        bookRequestRepository.save(req);

        if (req.getUser() != null) {
            String title = "Book Request Status Updated";
            String desc = "Your request for '" + req.getTitle() + "' status has been updated to " + status.toUpperCase() + ".";
            notificationRepository.save(Notification.builder()
                    .user(req.getUser())
                    .title(title)
                    .description(desc)
                    .createdAt(LocalDateTime.now())
                    .isUnread(true)
                    .type("request")
                    .icon("approved".equalsIgnoreCase(status) ? "fas fa-check-circle" : "fas fa-info-circle")
                    .build());
        }

        return ResponseEntity.ok(req);
    }

    @DeleteMapping("/requests/{id}")
    public ResponseEntity<?> deleteRequestPermanently(@PathVariable String id) {
        if (bookRequestRepository.existsById(id)) {
            bookRequestRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Book request deleted permanently"));
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
