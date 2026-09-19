package com.bookvault.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookvault.backend.entity.BookRequest;
import com.bookvault.backend.entity.User;

import java.util.List;

@Repository
public interface BookRequestRepository extends JpaRepository<BookRequest, String> {
    List<BookRequest> findByUser(User user);
    List<BookRequest> findByStatus(String status);
}
