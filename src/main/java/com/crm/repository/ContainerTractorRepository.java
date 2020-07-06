package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.ContainerTractor;

@Repository
public interface ContainerTractorRepository extends JpaRepository<ContainerTractor, Long> {

}
