package com.bookvault.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "wishlists")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private LocalDate addedDate;

    public Wishlist() {}

    public Wishlist(String id, User user, Book book, LocalDate addedDate) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.addedDate = addedDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDate getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDate addedDate) { this.addedDate = addedDate; }

    public static WishlistBuilder builder() { return new WishlistBuilder(); }

    public static class WishlistBuilder {
        private String id;
        private User user;
        private Book book;
        private LocalDate addedDate;

        public WishlistBuilder id(String id) { this.id = id; return this; }
        public WishlistBuilder user(User user) { this.user = user; return this; }
        public WishlistBuilder book(Book book) { this.book = book; return this; }
        public WishlistBuilder addedDate(LocalDate addedDate) { this.addedDate = addedDate; return this; }

        public Wishlist build() {
            return new Wishlist(id, user, book, addedDate);
        }
    }
}
