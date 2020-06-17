package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.crm.models.Container;
import com.crm.models.Supplier;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

  @Query()
  Page<Supplier> findBySupplierId(Long supplierId, Pageable pageable);

}
