package com.bookvault.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookvault.backend.entity.Acquisition;

@Repository
public interface AcquisitionRepository extends JpaRepository<Acquisition, String> {
}
