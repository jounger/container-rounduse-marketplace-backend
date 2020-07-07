package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Geolocation;

@Repository
public interface GeolocationRepository extends JpaRepository<Geolocation, Long>{

}
