package com.bookvault.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bookvault.backend.entity.*;
import com.bookvault.backend.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/moderator")
public class ModeratorController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRequestRepository bookRequestRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // --- Dashboard & Activity ---
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("issuedBooks", borrowingRepository.findByStatus("BORROWED").size());
        stats.put("overdueLoans", borrowingRepository.findByStatus("OVERDUE").size());
        stats.put("pendingReservations", reservationRepository.findByStatus("ready").size() + reservationRepository.findByStatus("pending").size());
        stats.put("pendingRequests", bookRequestRepository.findByStatus("pending").size());
        stats.put("totalBooks", bookRepository.count());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/activity")
    public ResponseEntity<?> getActivity() {
        return ResponseEntity.ok(auditLogRepository.findAllByOrderByTimestampDesc());
    }

    // --- Issue & Returns Desk ---
    @GetMapping("/borrowing")
    public ResponseEntity<List<Borrowing>> getBorrowings() {
        return ResponseEntity.ok(borrowingRepository.findAll());
    }

    @GetMapping("/borrowings")
    public ResponseEntity<List<Borrowing>> getBorrowingsPlural() {
        return ResponseEntity.ok(borrowingRepository.findAll());
    }

    @PostMapping("/borrowing/issue")
    public ResponseEntity<?> issueBook(@RequestBody Map<String, String> payload, Authentication authentication) {
        String studentEmail = payload.get("studentEmail");
        String bookId = payload.get("bookId");

        User student = null;
        if (studentEmail != null && !studentEmail.trim().isEmpty()) {
            student = userRepository.findByEmail(studentEmail).orElse(null);
            if (student == null) {
                student = userRepository.findById(studentEmail).orElse(null);
            }
            if (student == null) {
                String searchKey = studentEmail.trim().toLowerCase();
                student = userRepository.findAll().stream()
                    .filter(u -> u.getId().toLowerCase().contains(searchKey) || 
                                 u.getEmail().toLowerCase().contains(searchKey) || 
                                 (u.getName() != null && u.getName().toLowerCase().contains(searchKey)))
                    .findFirst().orElse(null);
            }
        }
        if (student == null) {
            student = userRepository.findByEmail("student@university.edu").orElse(
                userRepository.findAll().stream().findFirst().orElse(null)
            );
        }
        if (student == null) return ResponseEntity.badRequest().body("Student not found");

        Book book = null;
        if (bookId != null && !bookId.trim().isEmpty()) {
            book = bookRepository.findById(bookId).orElse(null);
            if (book == null) {
                String bKey = bookId.trim().toLowerCase();
                book = bookRepository.findAll().stream()
                    .filter(b -> b.getId().toLowerCase().equals(bKey) || 
                                 (b.getIsbn() != null && b.getIsbn().toLowerCase().contains(bKey)) ||
                                 (b.getTitle() != null && b.getTitle().toLowerCase().contains(bKey)))
                    .findFirst().orElse(null);
            }
        }
        if (book == null) return ResponseEntity.badRequest().body("Book not found");

        if (book.getPhysicalAvailable() == null || book.getPhysicalAvailable() <= 0) {
            return ResponseEntity.badRequest().body("No available physical copies for this book");
        }

        book.setPhysicalAvailable(book.getPhysicalAvailable() - 1);
        bookRepository.save(book);

        String dueDateStr = payload.get("dueDate");
        LocalDate dueDate = LocalDate.now().plusDays(14);
        if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
            try {
                dueDate = LocalDate.parse(dueDateStr.trim());
            } catch (Exception e) {
                // Keep default
            }
        }

        Borrowing borrowing = Borrowing.builder()
                .user(student)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(dueDate)
                .isOverdue(dueDate.isBefore(LocalDate.now()))
                .progress(0)
                .status("BORROWED")
                .build();

        borrowingRepository.save(borrowing);

        // Audit Log
        AuditLog log = AuditLog.builder()
                .userEmail(authentication != null ? authentication.getName() : "moderator@university.edu")
                .action("Issue Book")
                .category("BookManagement")
                .ipAddress("127.0.0.1")
                .timestamp(LocalDateTime.now())
                .details("Issued '" + book.getTitle() + "' to student " + student.getEmail())
                .build();
        auditLogRepository.save(log);

        return ResponseEntity.ok(borrowing);
    }

    @PutMapping("/borrowing/{id}/renew")
    public ResponseEntity<?> renewBorrowing(@PathVariable String id, @RequestBody(required = false) Map<String, String> payload) {
        Borrowing borrowing = borrowingRepository.findById(id).orElse(null);
        if (borrowing == null) return ResponseEntity.notFound().build();

        if (payload != null && payload.containsKey("dueDate")) {
            try {
                borrowing.setDueDate(LocalDate.parse(payload.get("dueDate")));
            } catch (Exception e) {
                borrowing.setDueDate(borrowing.getDueDate() != null ? borrowing.getDueDate().plusDays(14) : LocalDate.now().plusDays(14));
            }
        } else {
            borrowing.setDueDate(borrowing.getDueDate() != null ? borrowing.getDueDate().plusDays(14) : LocalDate.now().plusDays(14));
        }
        borrowing.setIsOverdue(borrowing.getDueDate().isBefore(LocalDate.now()));
        borrowingRepository.save(borrowing);

        return ResponseEntity.ok(borrowing);
    }

    @PostMapping("/returns/process")
    public ResponseEntity<?> processReturn(@RequestBody Map<String, String> payload, Authentication authentication) {
        String borrowingId = payload.get("borrowingId");
        Borrowing borrowing = borrowingRepository.findById(borrowingId).orElse(null);
        if (borrowing == null) return ResponseEntity.notFound().build();

        borrowing.setReturnDate(LocalDate.now());
        borrowing.setStatus("RETURNED");
        borrowingRepository.save(borrowing);

        Book book = borrowing.getBook();
        if (book != null) {
            book.setPhysicalAvailable((book.getPhysicalAvailable() != null ? book.getPhysicalAvailable() : 0) + 1);
            bookRepository.save(book);
        }

        // Audit Log
        AuditLog log = AuditLog.builder()
                .userEmail(authentication != null ? authentication.getName() : "moderator@university.edu")
                .action("Process Return")
                .category("BookManagement")
                .ipAddress("127.0.0.1")
                .timestamp(LocalDateTime.now())
                .details("Processed return for '" + (book != null ? book.getTitle() : "Book") + "' from " + borrowing.getUser().getEmail())
                .build();
        auditLogRepository.save(log);

        return ResponseEntity.ok(Map.of("message", "Return processed successfully", "borrowing", borrowing));
    }

    // --- Inventory Management ---
    @GetMapping("/inventory")
    public ResponseEntity<List<Book>> getInventory() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @PutMapping("/inventory/{id}/location")
    public ResponseEntity<?> updateLocation(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) return ResponseEntity.notFound().build();

        if (payload.containsKey("physicalStacks")) book.setPhysicalStacks(payload.get("physicalStacks").toString());
        if (payload.containsKey("physicalCopies")) book.setPhysicalCopies((Integer) payload.get("physicalCopies"));

        bookRepository.save(book);
        return ResponseEntity.ok(book);
    }

    @PostMapping("/books")
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        if (book.getId() == null) book.setId("b" + (bookRepository.count() + 1));
        if (book.getPhysicalAvailable() == null) book.setPhysicalAvailable(book.getPhysicalCopies());
        bookRepository.save(book);
        return ResponseEntity.ok(book);
    }

    // --- Reservations Queue ---
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

        Notification n = Notification.builder()
                .user(res.getUser())
                .title("Reservation Ready for Pickup! 📚")
                .description("Your reservation for '" + res.getBook().getTitle() + "' is ready at the circulation desk. Please pick it up by " + res.getPickupDeadline() + ".")
                .createdAt(LocalDateTime.now())
                .isUnread(true)
                .type("RESERVATION_READY")
                .icon("fas fa-box-open")
                .build();
        notificationRepository.save(n);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/reservations/{id}/checkout")
    public ResponseEntity<?> checkoutReservation(@PathVariable String id, Authentication authentication) {
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

        Notification n = Notification.builder()
                .user(res.getUser())
                .title("Book Checked Out 📖")
                .description("Your reservation for '" + book.getTitle() + "' has been converted to an active loan due on " + borrowing.getDueDate() + ".")
                .createdAt(LocalDateTime.now())
                .isUnread(true)
                .type("RESERVATION_FULFILLED")
                .icon("fas fa-barcode")
                .build();
        notificationRepository.save(n);

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

    // --- Student Requests Review ---
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

    // --- Fines Desk ---
    @GetMapping("/fines")
    public ResponseEntity<List<Fine>> getFines() {
        return ResponseEntity.ok(fineRepository.findAll());
    }

    @PostMapping("/fines/{id}/collect")
    public ResponseEntity<?> collectFine(@PathVariable String id) {
        Fine fine = fineRepository.findById(id).orElse(null);
        if (fine == null) return ResponseEntity.notFound().build();

        fine.setStatus("PAID");
        fine.setDatePaid(LocalDate.now());
        fineRepository.save(fine);
        return ResponseEntity.ok(fine);
    }

    // --- Student Lookup ---
    @GetMapping("/students")
    public ResponseEntity<?> getStudents() {
        List<User> students = userRepository.findByRole(Role.STUDENT);
        List<Borrowing> borrowings = borrowingRepository.findAll();
        List<Fine> fines = fineRepository.findAll();

        List<Map<String, Object>> result = students.stream().map(student -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", student.getId());
            map.put("studentId", student.getStudentId() != null ? student.getStudentId() : student.getId());
            map.put("name", student.getName());
            map.put("email", student.getEmail());
            map.put("department", student.getDepartment() != null ? student.getDepartment() : "General");
            map.put("phone", student.getPhone());
            map.put("role", student.getRole());
            map.put("status", student.getStatus());

            long activeBorrowCount = borrowings.stream()
                .filter(b -> b.getUser() != null && (
                    student.getId().equalsIgnoreCase(b.getUser().getId()) ||
                    student.getEmail().equalsIgnoreCase(b.getUser().getEmail()) ||
                    (student.getStudentId() != null && student.getStudentId().equalsIgnoreCase(b.getUser().getStudentId()))
                ))
                .filter(b -> !"RETURNED".equalsIgnoreCase(b.getStatus()) && !"COMPLETED".equalsIgnoreCase(b.getStatus()))
                .count();

            double unpaidFinesTotal = fines.stream()
                .filter(f -> f.getUser() != null && (
                    student.getId().equalsIgnoreCase(f.getUser().getId()) ||
                    student.getEmail().equalsIgnoreCase(f.getUser().getEmail()) ||
                    (student.getStudentId() != null && student.getStudentId().equalsIgnoreCase(f.getUser().getStudentId()))
                ))
                .filter(f -> "PENDING".equalsIgnoreCase(f.getStatus()) || "UNPAID".equalsIgnoreCase(f.getStatus()))
                .mapToDouble(f -> f.getAmount() != null ? f.getAmount() : 0.0)
                .sum();

            map.put("currentlyBorrowed", activeBorrowCount);
            map.put("fines", unpaidFinesTotal);
            return map;
        }).collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
