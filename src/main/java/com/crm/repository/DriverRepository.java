package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long>{

}
