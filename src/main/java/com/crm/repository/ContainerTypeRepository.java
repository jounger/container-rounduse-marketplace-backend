package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.ContainerType;

@Repository
public interface ContainerTypeRepository extends JpaRepository<ContainerType, Long>{

}
