package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Booking;
import com.crm.payload.request.PaginationRequest;

public interface BookingService {

  Booking getBookingByOutbound(Long id);

  Booking getBookingById(Long id);

  Booking getBookingsByNumber(String number);

  Page<Booking> searchBookings(PaginationRequest request, String search);

  Booking editBooking(Map<String, Object> updates, Long id, String username);
}
