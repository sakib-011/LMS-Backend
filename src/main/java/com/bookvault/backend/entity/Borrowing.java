package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "borrowings")
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private LocalDate borrowDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    private Boolean isOverdue;

    private Integer progress;

    private String status;

    private Double fineAmount;

    private Boolean finePaid;

    public Borrowing() {}

    public Borrowing(String id, User user, Book book, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, Boolean isOverdue, Integer progress, String status, Double fineAmount, Boolean finePaid) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.isOverdue = isOverdue;
        this.progress = progress;
        this.status = status;
        this.fineAmount = fineAmount;
        this.finePaid = finePaid;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getFineAmount() { return fineAmount; }
    public void setFineAmount(Double fineAmount) { this.fineAmount = fineAmount; }

    public Boolean getFinePaid() { return finePaid; }
    public void setFinePaid(Boolean finePaid) { this.finePaid = finePaid; }

    public static BorrowingBuilder builder() { return new BorrowingBuilder(); }

    public static class BorrowingBuilder {
        private String id;
        private User user;
        private Book book;
        private LocalDate borrowDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        private Boolean isOverdue;
        private Integer progress;
        private String status;
        private Double fineAmount;
        private Boolean finePaid;

        public BorrowingBuilder id(String id) { this.id = id; return this; }
        public BorrowingBuilder user(User user) { this.user = user; return this; }
        public BorrowingBuilder book(Book book) { this.book = book; return this; }
        public BorrowingBuilder borrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; return this; }
        public BorrowingBuilder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public BorrowingBuilder returnDate(LocalDate returnDate) { this.returnDate = returnDate; return this; }
        public BorrowingBuilder isOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; return this; }
        public BorrowingBuilder progress(Integer progress) { this.progress = progress; return this; }
        public BorrowingBuilder status(String status) { this.status = status; return this; }
        public BorrowingBuilder fineAmount(Double fineAmount) { this.fineAmount = fineAmount; return this; }
        public BorrowingBuilder finePaid(Boolean finePaid) { this.finePaid = finePaid; return this; }

        public Borrowing build() {
            return new Borrowing(id, user, book, borrowDate, dueDate, returnDate, isOverdue, progress, status, fineAmount, finePaid);
        }
    }
}
