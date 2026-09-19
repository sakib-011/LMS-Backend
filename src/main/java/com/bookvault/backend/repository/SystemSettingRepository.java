package com.bookvault.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookvault.backend.entity.SystemSetting;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, String> {
}
