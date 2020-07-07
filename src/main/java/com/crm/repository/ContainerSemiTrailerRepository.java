package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.ContainerSemiTrailer;

@Repository
public interface ContainerSemiTrailerRepository extends JpaRepository<ContainerSemiTrailer, Long> {

}
