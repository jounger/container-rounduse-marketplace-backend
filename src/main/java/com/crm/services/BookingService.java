package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Booking;
import com.crm.payload.request.BookingRequest;
import com.crm.payload.request.PaginationRequest;

public interface BookingService {

  Page<Booking> getBookingsByOutbound(Long id, PaginationRequest request);

  Booking getBookingById(Long id);

  Booking getBookingsByBookingNumber(String bookingNumber);

  Page<Booking> searchBookings(PaginationRequest request, String search);

  Booking updateBooking(Long id, BookingRequest request);

  Booking editBooking(Map<String, Object> updates, Long id, Long userId);
}
