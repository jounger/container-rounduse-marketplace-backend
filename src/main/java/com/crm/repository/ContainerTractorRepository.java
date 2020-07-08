package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.ContainerTractor;

@Repository
public interface ContainerTractorRepository extends JpaRepository<ContainerTractor, Long> {

  Optional<ContainerTractor> findByLicensePlate(String licensePlate);

}
