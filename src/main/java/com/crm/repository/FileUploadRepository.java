package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.FileUpload;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

  Optional<FileUpload> findByName(String name);
}
