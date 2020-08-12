package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crm.models.FileUpload;

public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

  Optional<FileUpload> findByName(String name);
}
