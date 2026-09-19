package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private Integer rating;

    @Column(length = 1500)
    private String comment;

    private LocalDate date;

    private Integer helpful;

    public Review() {}

    public Review(String id, User user, Book book, Integer rating, String comment, LocalDate date, Integer helpful) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.helpful = helpful;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getHelpful() { return helpful; }
    public void setHelpful(Integer helpful) { this.helpful = helpful; }

    public static ReviewBuilder builder() { return new ReviewBuilder(); }

    public static class ReviewBuilder {
        private String id;
        private User user;
        private Book book;
        private Integer rating;
        private String comment;
        private LocalDate date;
        private Integer helpful;

        public ReviewBuilder id(String id) { this.id = id; return this; }
        public ReviewBuilder user(User user) { this.user = user; return this; }
        public ReviewBuilder book(Book book) { this.book = book; return this; }
        public ReviewBuilder rating(Integer rating) { this.rating = rating; return this; }
        public ReviewBuilder comment(String comment) { this.comment = comment; return this; }
        public ReviewBuilder date(LocalDate date) { this.date = date; return this; }
        public ReviewBuilder helpful(Integer helpful) { this.helpful = helpful; return this; }

        public Review build() {
            return new Review(id, user, book, rating, comment, date, helpful);
        }
    }
}
