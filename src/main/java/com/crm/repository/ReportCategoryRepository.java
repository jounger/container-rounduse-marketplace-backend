package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crm.models.ReportCategory;

public interface ReportCategoryRepository extends JpaRepository<ReportCategory, Long> {

  Boolean existsByName(String name);

  Optional<ReportCategory> findByName(String name);
}
