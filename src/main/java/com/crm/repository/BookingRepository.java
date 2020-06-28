package com.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crm.models.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{

  Boolean existsByBookingNumber(String bookingNumber);
}
