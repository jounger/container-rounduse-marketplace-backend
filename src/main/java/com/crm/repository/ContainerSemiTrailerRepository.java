package com.crm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.ContainerSemiTrailer;

@Repository
public interface ContainerSemiTrailerRepository
    extends JpaRepository<ContainerSemiTrailer, Long>, JpaSpecificationExecutor<ContainerSemiTrailer> {

  Optional<ContainerSemiTrailer> findByLicensePlate(String licensePlate);

  @Query(value = "FROM ContainerSemiTrailer c WHERE c.forwarder.username = :username")
  Page<ContainerSemiTrailer> findByForwarder(@Param("username") String username, Pageable pageable);
}
