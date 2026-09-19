package com.bookvault.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookvault.backend.entity.Book;
import com.bookvault.backend.entity.DigitalBook;
import com.bookvault.backend.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface DigitalBookRepository extends JpaRepository<DigitalBook, String> {
    List<DigitalBook> findByUser(User user);
    Optional<DigitalBook> findByUserAndBook(User user, Book book);
}
