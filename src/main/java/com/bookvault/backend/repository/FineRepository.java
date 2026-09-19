package com.bookvault.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookvault.backend.entity.Fine;
import com.bookvault.backend.entity.User;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, String> {
    List<Fine> findByUser(User user);
    List<Fine> findByStatus(String status);
}
