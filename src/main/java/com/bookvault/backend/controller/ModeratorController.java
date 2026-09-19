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
        }
        if (student == null) {
            student = userRepository.findByEmail("student@university.edu").orElse(
                userRepository.findAll().stream().findFirst().orElse(null)
            );
        }
        if (student == null) return ResponseEntity.badRequest().body("Student not found");

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return ResponseEntity.badRequest().body("Book not found");

        if (book.getPhysicalAvailable() <= 0) {
            return ResponseEntity.badRequest().body("No available physical copies for this book");
        }

        book.setPhysicalAvailable(book.getPhysicalAvailable() - 1);
        bookRepository.save(book);

        Borrowing borrowing = Borrowing.builder()
                .user(student)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .isOverdue(false)
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

    @PostMapping("/returns/process")
    public ResponseEntity<?> processReturn(@RequestBody Map<String, String> payload, Authentication authentication) {
        String borrowingId = payload.get("borrowingId");
        Borrowing borrowing = borrowingRepository.findById(borrowingId).orElse(null);
        if (borrowing == null) return ResponseEntity.notFound().build();

        borrowing.setReturnDate(LocalDate.now());
        borrowing.setStatus("RETURNED");
        borrowingRepository.save(borrowing);

        Book book = borrowing.getBook();
        book.setPhysicalAvailable(book.getPhysicalAvailable() + 1);
        bookRepository.save(book);

        // Audit Log
        AuditLog log = AuditLog.builder()
                .userEmail(authentication != null ? authentication.getName() : "moderator@university.edu")
                .action("Process Return")
                .category("BookManagement")
                .ipAddress("127.0.0.1")
                .timestamp(LocalDateTime.now())
                .details("Processed return for '" + book.getTitle() + "' from " + borrowing.getUser().getEmail())
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
        return ResponseEntity.ok(res);
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
    public ResponseEntity<List<User>> getStudents() {
        return ResponseEntity.ok(userRepository.findByRole(Role.STUDENT));
    }
}
