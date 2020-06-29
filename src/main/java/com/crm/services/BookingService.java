package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Booking;
import com.crm.payload.request.BookingRequest;
import com.crm.payload.request.PaginationRequest;

public interface BookingService {

  Page<Booking> getBookingsByOutbound(Long id, PaginationRequest request);

  Booking updateBooking(BookingRequest request);

  Booking editBooking(Map<String, Object> updates, Long id);
}
