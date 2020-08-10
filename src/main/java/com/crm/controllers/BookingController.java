package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Booking;
import com.crm.models.dto.BookingDto;
import com.crm.models.mapper.BookingMapper;
import com.crm.payload.request.BookingRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.BookingService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/booking")
public class BookingController {

  @Autowired
  BookingService bookingService;

  @GetMapping("/outbound/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getBookingByOutbound(@PathVariable Long id, @Valid PaginationRequest request) {

    Booking booking = bookingService.getBookingByOutbound(id);
    BookingDto bookingDto = BookingMapper.toBookingDto(booking);
    return ResponseEntity.ok(bookingDto);
  }

  @GetMapping("/filter")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> searchBookings(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {

    Page<Booking> pages = bookingService.searchBookings(request, search);
    PaginationResponse<BookingDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Booking> bookings = pages.getContent();
    List<BookingDto> bookingsDto = new ArrayList<>();
    bookings.forEach(booking -> bookingsDto.add(BookingMapper.toBookingDto(booking)));
    response.setContents(bookingsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getBooking(@PathVariable Long id) {
    Booking booking = bookingService.getBookingById(id);
    BookingDto bookingDto = BookingMapper.toBookingDto(booking);
    return ResponseEntity.ok(bookingDto);
  }

  @RequestMapping(method = RequestMethod.GET, params = { "number" })
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getBookingsByNumber(@RequestParam String number) {
    Booking booking = bookingService.getBookingsByNumber(number);
    BookingDto bookingDto = BookingMapper.toBookingDto(booking);
    return ResponseEntity.ok(bookingDto);
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> updateBooking(@Valid @RequestBody BookingRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    Booking booking = bookingService.updateBooking(username, request);
    BookingDto bookingDto = BookingMapper.toBookingDto(booking);
    return ResponseEntity.ok(bookingDto);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> editBooking(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    Booking booking = bookingService.editBooking(updates, id, username);
    BookingDto bookingDto = new BookingDto();
    bookingDto = BookingMapper.toBookingDto(booking);
    return ResponseEntity.ok(bookingDto);
  }
}
