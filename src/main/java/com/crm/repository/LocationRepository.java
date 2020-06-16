package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>{

}
