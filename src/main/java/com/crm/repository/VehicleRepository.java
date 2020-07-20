package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

  Boolean existsByLicensePlate(String licensePlate);
}
