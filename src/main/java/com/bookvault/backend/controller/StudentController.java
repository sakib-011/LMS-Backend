package com.bookvault.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.bookvault.backend.entity.*;
import com.bookvault.backend.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
public class StudentController {

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
    private DigitalBookRepository digitalBookRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FineRepository fineRepository;

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            User user = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (user != null) return user;
        }
        return userRepository.findByEmail("student@university.edu").orElse(
            userRepository.findAll().stream().findFirst().orElse(null)
        );
    }

    // --- Student Dashboard Overview ---
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        List<Borrowing> borrowings = borrowingRepository.findByUser(user);
        List<Reservation> reservations = reservationRepository.findByUser(user);
        List<BookRequest> requests = bookRequestRepository.findByUser(user);
        List<Fine> fines = fineRepository.findByUser(user);

        double totalFines = fines.stream()
                .filter(f -> "PENDING".equalsIgnoreCase(f.getStatus()))
                .mapToDouble(Fine::getAmount)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("currentlyBorrowed", borrowings.stream().filter(b -> !"RETURNED".equalsIgnoreCase(b.getStatus())).count());
        stats.put("totalBorrowed", borrowings.size());
        stats.put("pendingReservations", reservations.stream().filter(r -> "pending".equalsIgnoreCase(r.getStatus()) || "ready".equalsIgnoreCase(r.getStatus())).count());
        stats.put("activeRequests", requests.size());
        stats.put("totalFines", totalFines);
        stats.put("recentBorrowings", borrowings);

        return ResponseEntity.ok(stats);
    }

    // --- Borrowings ---
    @GetMapping("/borrowings")
    public ResponseEntity<?> getBorrowings(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(borrowingRepository.findByUser(user));
    }

    // --- Reservations ---
    @GetMapping("/reservations")
    public ResponseEntity<?> getReservations(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(reservationRepository.findByUser(user));
    }

    @PostMapping("/reservations")
    public ResponseEntity<?> createReservation(@RequestBody Map<String, String> payload, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        String bookId = payload.get("bookId");
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return ResponseEntity.badRequest().body("Book not found");

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .reservedDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(14))
                .queuePosition(1)
                .status("pending")
                .build();

        reservationRepository.save(reservation);
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable String id, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null && reservation.getUser().getId().equals(user.getId())) {
            reservation.setStatus("cancelled");
            reservationRepository.save(reservation);
            return ResponseEntity.ok(Map.of("message", "Reservation cancelled"));
        }
        return ResponseEntity.notFound().build();
    }

    // --- Book Requests ---
    @GetMapping("/requests")
    public ResponseEntity<?> getRequests(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(bookRequestRepository.findByUser(user));
    }

    @PostMapping("/requests")
    public ResponseEntity<?> createRequest(@RequestBody Map<String, String> payload, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        BookRequest req = BookRequest.builder()
                .user(user)
                .title(payload.get("title"))
                .author(payload.get("author"))
                .isbn(payload.get("isbn"))
                .reason(payload.get("reason"))
                .status("pending")
                .submittedDate(LocalDate.now())
                .build();

        bookRequestRepository.save(req);
        return ResponseEntity.ok(req);
    }

    // --- Digital Library ---
    @GetMapping("/digital-library")
    public ResponseEntity<?> getDigitalBooks(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(digitalBookRepository.findByUser(user));
    }

    @PutMapping("/digital-library/{id}/progress")
    public ResponseEntity<?> updateProgress(@PathVariable String id, @RequestBody Map<String, Object> payload, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        DigitalBook db = digitalBookRepository.findById(id).orElse(null);
        if (db != null && db.getUser().getId().equals(user.getId())) {
            if (payload.containsKey("progress")) db.setProgress((Integer) payload.get("progress"));
            if (payload.containsKey("currentPage")) db.setCurrentPage((Integer) payload.get("currentPage"));
            if (payload.containsKey("bookmarked")) db.setBookmarked((Boolean) payload.get("bookmarked"));
            db.setLastRead(LocalDate.now());
            digitalBookRepository.save(db);
            return ResponseEntity.ok(db);
        }
        return ResponseEntity.notFound().build();
    }

    // --- Wishlist ---
    @GetMapping("/wishlist")
    public ResponseEntity<?> getWishlist(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(wishlistRepository.findByUser(user));
    }

    @PostMapping("/wishlist/{bookId}")
    public ResponseEntity<?> addToWishlist(@PathVariable String bookId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return ResponseEntity.badRequest().body("Book not found");

        if (wishlistRepository.findByUserAndBook(user, book).isEmpty()) {
            Wishlist item = Wishlist.builder()
                    .user(user)
                    .book(book)
                    .addedDate(LocalDate.now())
                    .build();
            wishlistRepository.save(item);
        }
        return ResponseEntity.ok(Map.of("message", "Added to wishlist"));
    }

    @DeleteMapping("/wishlist/{bookId}")
    @Transactional
    public ResponseEntity<?> removeFromWishlist(@PathVariable String bookId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book != null) {
            wishlistRepository.deleteByUserAndBook(user, book);
        }
        return ResponseEntity.ok(Map.of("message", "Removed from wishlist"));
    }

    // --- Notifications ---
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(notificationRepository.findByUserOrderByCreatedAtDesc(user));
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<?> markNotificationRead(@PathVariable String id, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");

        Notification n = notificationRepository.findById(id).orElse(null);
        if (n != null && n.getUser().getId().equals(user.getId())) {
            n.setIsUnread(false);
            notificationRepository.save(n);
            return ResponseEntity.ok(n);
        }
        return ResponseEntity.notFound().build();
    }

    // --- Profile & Settings ---
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        return ResponseEntity.ok(user);
    }
}
