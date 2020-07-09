package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Booking;
import com.crm.models.Port;
import com.crm.payload.request.BookingRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BookingRepository;
import com.crm.repository.MerchantRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.PortRepository;
import com.crm.services.BookingService;
import com.crm.specification.builder.BookingSpecificationsBuilder;

@Service
public class BookingServiceImpl implements BookingService {

  @Autowired
  BookingRepository bookingRepository;

  @Autowired
  MerchantRepository merchantRepository;

  @Autowired
  private PortRepository portRepository;

  @Autowired
  private OutboundRepository outboundRepository;

  @Override
  public Page<Booking> getBookingsByOutbound(Long id, PaginationRequest request) {
    if (outboundRepository.existsById(id)) {
      PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
          Sort.by(Sort.Direction.DESC, "createdAt"));
      Page<Booking> pages = bookingRepository.findByOutbound(id, pageRequest);
      return pages;

    } else {
      throw new NotFoundException("ERROR: Outbound is not found.");
    }
  }

  @Override
  public Booking updateBooking(Long id, BookingRequest request) {
    if (merchantRepository.existsById(id)) {

      Booking booking = bookingRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("ERROR: Booking is not found."));

      if (!booking.getOutbound().getMerchant().getId().equals(id)) {
        throw new InternalException(String.format("Merchant %s not owned Booking", id));
      }

      if (booking.getOutbound().getStatus().equals(EnumSupplyStatus.COMBINED.name())
          || booking.getOutbound().getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(String.format("Outbound with bookingNumber %s has been %s",
            booking.getBookingNumber(), booking.getOutbound().getStatus()));
      }

      String bookingNumber = request.getBookingNumber();
      if (bookingNumber != null && !bookingNumber.isEmpty()) {
        if (bookingRepository.existsByBookingNumber(bookingNumber)) {
          if (bookingNumber.equals(booking.getBookingNumber())) {
          } else {
            throw new DuplicateRecordException("Error: Booking has been existed");
          }
        }
        booking.setBookingNumber(bookingNumber);
      }

      Port portOfLoading = portRepository.findByNameCode(request.getPortOfLoading())
          .orElseThrow(() -> new NotFoundException("ERROR: PortOfLoading is not found."));
      booking.setPortOfLoading(portOfLoading);

      booking.setUnit(request.getUnit());

      if (request.getCutOffTime() != null && !request.getCutOffTime().isEmpty()) {
        LocalDateTime cutOffTime = Tool.convertToLocalDateTime(request.getCutOffTime());
        booking.setCutOffTime(cutOffTime);
      }

      booking.setIsFcl(request.getIsFcl());

      bookingRepository.save(booking);
      return booking;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public Booking editBooking(Map<String, Object> updates, Long id, Long userId) {

    if (merchantRepository.existsById(userId)) {
      Booking booking = bookingRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: Booking is not found."));

      if (!booking.getOutbound().getMerchant().getId().equals(userId)) {
        throw new InternalException(String.format("Merchant %s not owned Booking", id));
      }

      if (booking.getOutbound().getStatus().equals(EnumSupplyStatus.COMBINED.name())
          || booking.getOutbound().getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(String.format("Outbound with bookingNumber %s has been %s",
            booking.getBookingNumber(), booking.getOutbound().getStatus()));
      }

      String portOfLoadingRequest = (String) updates.get("portOfLoading");
      if (portOfLoadingRequest != null && !portOfLoadingRequest.isEmpty()) {
        Port portOfLoading = portRepository.findByNameCode(portOfLoadingRequest)
            .orElseThrow(() -> new NotFoundException("ERROR: PortOfLoading is not found."));
        booking.setPortOfLoading(portOfLoading);
      }

      String bookingNumberRequest = (String) updates.get("bookingNumber");
      if (bookingNumberRequest != null && !bookingNumberRequest.isEmpty()) {
        if (bookingRepository.existsByBookingNumber(bookingNumberRequest)) {
          if (bookingNumberRequest.equals(booking.getBookingNumber())) {
          } else {
            throw new DuplicateRecordException("Error: Booking has been existed");
          }
        }
        booking.setBookingNumber(bookingNumberRequest);
      }

      Integer unit = (Integer) updates.get("unit");
      if (unit != null) {
        booking.setUnit(unit);
      }

      String cutOffTimeRequest = (String) updates.get("cutOffTime");
      if (cutOffTimeRequest != null && !cutOffTimeRequest.isEmpty()) {
        LocalDateTime cutOffTime = Tool.convertToLocalDateTime(cutOffTimeRequest);
        booking.setCutOffTime(cutOffTime);
      }

      Boolean isFcl = (Boolean) updates.get("isFcl");
      if (isFcl != null) {
        booking.setIsFcl(isFcl);
      }

      bookingRepository.save(booking);
      return booking;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public Booking getBookingById(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Booking is not found."));
    return booking;
  }

  @Override
  public Booking getBookingsByBookingNumber(String bookingNumber) {
    Booking booking = bookingRepository.findByBookingNumber(bookingNumber)
        .orElseThrow(() -> new NotFoundException("ERROR: Booking is not found."));
    return booking;
  }

  @Override
  public Page<Booking> searchBookings(PaginationRequest request, String search) {
    BookingSpecificationsBuilder builder = new BookingSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Booking> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Booking> pages = bookingRepository.findAll(spec, page);
    // Return result
    return pages;
  }
}
