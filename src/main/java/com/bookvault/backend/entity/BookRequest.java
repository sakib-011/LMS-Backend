package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "book_requests")
public class BookRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String isbn;

    @Column(length = 1000)
    private String reason;

    private String status;

    private LocalDate submittedDate;

    private String notes;

    public BookRequest() {}

    public BookRequest(String id, User user, String title, String author, String isbn, String reason, String status, LocalDate submittedDate, String notes) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.reason = reason;
        this.status = status;
        this.submittedDate = submittedDate;
        this.notes = notes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static BookRequestBuilder builder() { return new BookRequestBuilder(); }

    public static class BookRequestBuilder {
        private String id;
        private User user;
        private String title;
        private String author;
        private String isbn;
        private String reason;
        private String status;
        private LocalDate submittedDate;
        private String notes;

        public BookRequestBuilder id(String id) { this.id = id; return this; }
        public BookRequestBuilder user(User user) { this.user = user; return this; }
        public BookRequestBuilder title(String title) { this.title = title; return this; }
        public BookRequestBuilder author(String author) { this.author = author; return this; }
        public BookRequestBuilder isbn(String isbn) { this.isbn = isbn; return this; }
        public BookRequestBuilder reason(String reason) { this.reason = reason; return this; }
        public BookRequestBuilder status(String status) { this.status = status; return this; }
        public BookRequestBuilder submittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; return this; }
        public BookRequestBuilder notes(String notes) { this.notes = notes; return this; }

        public BookRequest build() {
            return new BookRequest(id, user, title, author, isbn, reason, status, submittedDate, notes);
        }
    }
}
