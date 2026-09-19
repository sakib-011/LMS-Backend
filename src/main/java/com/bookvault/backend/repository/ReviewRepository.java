package com.bookvault.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookvault.backend.entity.Book;
import com.bookvault.backend.entity.Review;
import com.bookvault.backend.entity.User;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByBook(Book book);
    List<Review> findByUser(User user);
}
