package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Container;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

  Boolean existsByContainerNumber(String containerNumber);

  Boolean existsByLicensePlate(String licensePlate);
  
  @Query(value = "SELECT b FROM Container b WHERE b.billOfLading.id = :id")
  Page<Container> getContainersByInbound(@Param("id") Long id, Pageable pageable);
  
}
