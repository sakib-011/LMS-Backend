package com.bookvault.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookvault.backend.entity.Book;
import com.bookvault.backend.entity.Reservation;
import com.bookvault.backend.entity.User;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByBookAndStatus(Book book, String status);
    List<Reservation> findByStatus(String status);
}
