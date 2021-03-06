package com.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.crm.models.ContainerType;

@Repository
public interface ContainerTypeRepository
    extends JpaRepository<ContainerType, Long>, JpaSpecificationExecutor<ContainerType> {

  Optional<ContainerType> findByName(String name);

  Boolean existsByName(String name);
}
