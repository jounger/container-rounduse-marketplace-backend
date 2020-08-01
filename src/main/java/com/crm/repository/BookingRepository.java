package com.crm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

  Boolean existsByNumber(String number);

  @Query(value = "SELECT b FROM Booking b WHERE b.outbound.id = :id")
  Page<Booking> findByOutbound(@Param("id") Long id, Pageable pageable);

  Optional<Booking> findByNumber(String number);
}
