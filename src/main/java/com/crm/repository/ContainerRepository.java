package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Container;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

  Boolean existsByContainerNumber(String containerNumber);

  Boolean existsByLicensePlate(String licensePlate);
}
