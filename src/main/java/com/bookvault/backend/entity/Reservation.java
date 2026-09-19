package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {

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
    private LocalDate reservedDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    private Integer queuePosition;

    private String status;

    private LocalDate pickupDeadline;

    public Reservation() {}

    public Reservation(String id, User user, Book book, LocalDate reservedDate, LocalDate expiryDate, Integer queuePosition, String status, LocalDate pickupDeadline) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.reservedDate = reservedDate;
        this.expiryDate = expiryDate;
        this.queuePosition = queuePosition;
        this.status = status;
        this.pickupDeadline = pickupDeadline;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDate getReservedDate() { return reservedDate; }
    public void setReservedDate(LocalDate reservedDate) { this.reservedDate = reservedDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPickupDeadline() { return pickupDeadline; }
    public void setPickupDeadline(LocalDate pickupDeadline) { this.pickupDeadline = pickupDeadline; }

    public static ReservationBuilder builder() { return new ReservationBuilder(); }

    public static class ReservationBuilder {
        private String id;
        private User user;
        private Book book;
        private LocalDate reservedDate;
        private LocalDate expiryDate;
        private Integer queuePosition;
        private String status;
        private LocalDate pickupDeadline;

        public ReservationBuilder id(String id) { this.id = id; return this; }
        public ReservationBuilder user(User user) { this.user = user; return this; }
        public ReservationBuilder book(Book book) { this.book = book; return this; }
        public ReservationBuilder reservedDate(LocalDate reservedDate) { this.reservedDate = reservedDate; return this; }
        public ReservationBuilder expiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; return this; }
        public ReservationBuilder queuePosition(Integer queuePosition) { this.queuePosition = queuePosition; return this; }
        public ReservationBuilder status(String status) { this.status = status; return this; }
        public ReservationBuilder pickupDeadline(LocalDate pickupDeadline) { this.pickupDeadline = pickupDeadline; return this; }

        public Reservation build() {
            return new Reservation(id, user, book, reservedDate, expiryDate, queuePosition, status, pickupDeadline);
        }
    }
}
