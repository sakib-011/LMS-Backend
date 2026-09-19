package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "fines")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrowing_id")
    private Borrowing borrowing;

    private Double amount;

    private String status;

    private String reason;

    private LocalDate dateIssued;

    private LocalDate datePaid;

    public Fine() {}

    public Fine(String id, User user, Borrowing borrowing, Double amount, String status, String reason, LocalDate dateIssued, LocalDate datePaid) {
        this.id = id;
        this.user = user;
        this.borrowing = borrowing;
        this.amount = amount;
        this.status = status;
        this.reason = reason;
        this.dateIssued = dateIssued;
        this.datePaid = datePaid;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Borrowing getBorrowing() { return borrowing; }
    public void setBorrowing(Borrowing borrowing) { this.borrowing = borrowing; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getDateIssued() { return dateIssued; }
    public void setDateIssued(LocalDate dateIssued) { this.dateIssued = dateIssued; }

    public LocalDate getDatePaid() { return datePaid; }
    public void setDatePaid(LocalDate datePaid) { this.datePaid = datePaid; }

    public static FineBuilder builder() { return new FineBuilder(); }

    public static class FineBuilder {
        private String id;
        private User user;
        private Borrowing borrowing;
        private Double amount;
        private String status;
        private String reason;
        private LocalDate dateIssued;
        private LocalDate datePaid;

        public FineBuilder id(String id) { this.id = id; return this; }
        public FineBuilder user(User user) { this.user = user; return this; }
        public FineBuilder borrowing(Borrowing borrowing) { this.borrowing = borrowing; return this; }
        public FineBuilder amount(Double amount) { this.amount = amount; return this; }
        public FineBuilder status(String status) { this.status = status; return this; }
        public FineBuilder reason(String reason) { this.reason = reason; return this; }
        public FineBuilder dateIssued(LocalDate dateIssued) { this.dateIssued = dateIssued; return this; }
        public FineBuilder datePaid(LocalDate datePaid) { this.datePaid = datePaid; return this; }

        public Fine build() {
            return new Fine(id, user, borrowing, amount, status, reason, dateIssued, datePaid);
        }
    }
}
