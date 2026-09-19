package com.bookvault.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookvault.backend.entity.Borrowing;
import com.bookvault.backend.entity.User;

import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, String> {
    List<Borrowing> findByUser(User user);
    List<Borrowing> findByUserAndStatus(User user, String status);
    List<Borrowing> findByStatus(String status);
}
