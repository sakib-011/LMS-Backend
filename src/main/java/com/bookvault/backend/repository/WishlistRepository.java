package com.bookvault.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookvault.backend.entity.Book;
import com.bookvault.backend.entity.User;
import com.bookvault.backend.entity.Wishlist;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, String> {
    List<Wishlist> findByUser(User user);
    Optional<Wishlist> findByUserAndBook(User user, Book book);
    void deleteByUserAndBook(User user, Book book);
}
