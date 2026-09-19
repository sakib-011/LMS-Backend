package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "digital_books")
public class DigitalBook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private LocalDate addedDate;

    private LocalDate lastRead;

    private Integer progress;

    private Boolean bookmarked;

    private Integer currentPage;

    private String fileUrl;

    public DigitalBook() {}

    public DigitalBook(String id, User user, Book book, LocalDate addedDate, LocalDate lastRead, Integer progress, Boolean bookmarked, Integer currentPage, String fileUrl) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.addedDate = addedDate;
        this.lastRead = lastRead;
        this.progress = progress;
        this.bookmarked = bookmarked;
        this.currentPage = currentPage;
        this.fileUrl = fileUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDate getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDate addedDate) { this.addedDate = addedDate; }

    public LocalDate getLastRead() { return lastRead; }
    public void setLastRead(LocalDate lastRead) { this.lastRead = lastRead; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Boolean getBookmarked() { return bookmarked; }
    public void setBookmarked(Boolean bookmarked) { this.bookmarked = bookmarked; }

    public Integer getCurrentPage() { return currentPage; }
    public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public static DigitalBookBuilder builder() { return new DigitalBookBuilder(); }

    public static class DigitalBookBuilder {
        private String id;
        private User user;
        private Book book;
        private LocalDate addedDate;
        private LocalDate lastRead;
        private Integer progress;
        private Boolean bookmarked;
        private Integer currentPage;
        private String fileUrl;

        public DigitalBookBuilder id(String id) { this.id = id; return this; }
        public DigitalBookBuilder user(User user) { this.user = user; return this; }
        public DigitalBookBuilder book(Book book) { this.book = book; return this; }
        public DigitalBookBuilder addedDate(LocalDate addedDate) { this.addedDate = addedDate; return this; }
        public DigitalBookBuilder lastRead(LocalDate lastRead) { this.lastRead = lastRead; return this; }
        public DigitalBookBuilder progress(Integer progress) { this.progress = progress; return this; }
        public DigitalBookBuilder bookmarked(Boolean bookmarked) { this.bookmarked = bookmarked; return this; }
        public DigitalBookBuilder currentPage(Integer currentPage) { this.currentPage = currentPage; return this; }
        public DigitalBookBuilder fileUrl(String fileUrl) { this.fileUrl = fileUrl; return this; }

        public DigitalBook build() {
            return new DigitalBook(id, user, book, addedDate, lastRead, progress, bookmarked, currentPage, fileUrl);
        }
    }
}
