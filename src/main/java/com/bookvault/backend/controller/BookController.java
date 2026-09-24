package com.bookvault.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bookvault.backend.entity.Book;
import com.bookvault.backend.entity.Review;
import com.bookvault.backend.entity.User;
import com.bookvault.backend.repository.BookRepository;
import com.bookvault.backend.repository.ReviewRepository;
import com.bookvault.backend.repository.UserRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Book>> getBooks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(bookRepository.searchBooks(search));
        }
        if (category != null && !category.trim().isEmpty()) {
            return ResponseEntity.ok(bookRepository.findByCategoryIgnoreCase(category));
        }
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String q) {
        return ResponseEntity.ok(bookRepository.searchBooks(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable String id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Step 1: Save Book Data ONLY (No Image File) ---
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book bookData) {
        if (bookData.getTitle() == null || bookData.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Title is required"));
        }
        if (bookData.getAuthor() == null || bookData.getAuthor().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Author is required"));
        }

        // Generate ID if missing
        if (bookData.getId() == null || bookData.getId().trim().isEmpty()) {
            bookData.setId("b" + (bookRepository.count() + 1) + "_" + System.currentTimeMillis() % 10000);
        }

        // Default availability logic
        if (bookData.getPhysicalCopies() == null) {
            bookData.setPhysicalCopies(1);
        }
        if (bookData.getPhysicalAvailable() == null) {
            bookData.setPhysicalAvailable(bookData.getPhysicalCopies());
        }

        // Ensure image URL remains NULL initially until Cloudinary upload step
        if (bookData.getImageUrl() == null) {
            bookData.setImageUrl(null);
        }

        Book savedBook = bookRepository.save(bookData);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Book created successfully");
        response.put("data", savedBook);

        return ResponseEntity.ok(response);
    }

    // --- Step 5: Update Database With Cloudinary Image URL + Public ID ---
    @PatchMapping("/{id}/image")
    public ResponseEntity<?> updateBookImage(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Book not found with ID: " + id));
        }

        String imageUrl = payload.get("imageUrl");
        String cloudinaryPublicId = payload.get("cloudinaryPublicId");

        if (imageUrl != null) {
            book.setImageUrl(imageUrl);
        }
        if (cloudinaryPublicId != null) {
            book.setCloudinaryPublicId(cloudinaryPublicId);
        }

        Book updatedBook = bookRepository.save(book);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Book image updated successfully");
        response.put("data", updatedBook);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/pdf")
    public ResponseEntity<?> updateBookPdf(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Book not found with ID: " + id));
        }

        String pdfUrl = payload.get("pdfUrl");
        if (pdfUrl != null) {
            book.setPdfUrl(pdfUrl);
            book.setHasDigital(true);
        }

        Book updatedBook = bookRepository.save(book);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Book PDF updated successfully");
        response.put("data", updatedBook);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(
            @PathVariable String id,
            @RequestBody Book payload) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        if (payload.getTitle() != null) book.setTitle(payload.getTitle());
        if (payload.getAuthor() != null) book.setAuthor(payload.getAuthor());
        if (payload.getCategory() != null) book.setCategory(payload.getCategory());
        if (payload.getIsbn() != null) book.setIsbn(payload.getIsbn());
        if (payload.getPublisher() != null) book.setPublisher(payload.getPublisher());
        if (payload.getYear() != null) book.setYear(payload.getYear());
        if (payload.getPages() != null) book.setPages(payload.getPages());
        if (payload.getPhysicalCopies() != null) book.setPhysicalCopies(payload.getPhysicalCopies());
        if (payload.getPhysicalAvailable() != null) book.setPhysicalAvailable(payload.getPhysicalAvailable());
        if (payload.getHasDigital() != null) book.setHasDigital(payload.getHasDigital());
        if (payload.getPdfUrl() != null) book.setPdfUrl(payload.getPdfUrl());
        if (payload.getImageUrl() != null) book.setImageUrl(payload.getImageUrl());
        if (payload.getDescription() != null) book.setDescription(payload.getDescription());

        Book updatedBook = bookRepository.save(book);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable String id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        bookRepository.delete(book);
        return ResponseEntity.ok(Map.of("success", true, "message", "Book deleted successfully", "id", id));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<Review>> getBookReviews(@PathVariable String id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reviewRepository.findByBook(book));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> createReview(
            @PathVariable String id,
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Integer rating = payload.get("rating") != null ? Integer.parseInt(payload.get("rating").toString()) : 5;
        String comment = payload.get("comment") != null ? payload.get("comment").toString() : "";

        Review review = Review.builder()
                .book(book)
                .user(user)
                .rating(rating)
                .comment(comment)
                .date(LocalDate.now())
                .helpful(0)
                .build();

        reviewRepository.save(review);
        return ResponseEntity.ok(review);
    }
}
