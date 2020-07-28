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
import com.crm.common.ErrorConstant;
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
      throw new NotFoundException(ErrorConstant.OUTBOUND_NOT_FOUND);
    }
  }

  @Override
  public Booking updateBooking(String username, BookingRequest request) {
    if (merchantRepository.existsByUsername(username)) {

      Booking booking = bookingRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException(ErrorConstant.BOOKING_NOT_FOUND));

      if (!booking.getOutbound().getMerchant().getUsername().equals(username)) {
        throw new InternalException(ErrorConstant.USER_ACCESS_DENIED);
      }

      if (booking.getOutbound().getStatus().equals(EnumSupplyStatus.COMBINED.name())
          || booking.getOutbound().getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(ErrorConstant.OUTBOUND_IS_IN_TRANSACTION);
      }

      String bookingNumber = request.getBookingNumber();
      if (bookingNumber != null && !bookingNumber.isEmpty()) {
        if (bookingRepository.existsByBookingNumber(bookingNumber)) {
          if (bookingNumber.equals(booking.getBookingNumber())) {
          } else {
            throw new DuplicateRecordException(ErrorConstant.BOOKING_ALREADY_EXISTS);
          }
        }
        booking.setBookingNumber(bookingNumber);
      }

      Port portOfLoading = portRepository.findByNameCode(request.getPortOfLoading())
          .orElseThrow(() -> new NotFoundException(ErrorConstant.PORT_NOT_FOUND));
      booking.setPortOfLoading(portOfLoading);

      booking.setUnit(request.getUnit());

      if (request.getCutOffTime() != null && !request.getCutOffTime().isEmpty()) {
        LocalDateTime cutOffTime = Tool.convertToLocalDateTime(request.getCutOffTime());
        booking.setCutOffTime(cutOffTime);
      }

      booking.setIsFcl(request.getIsFcl());

      Booking _booking = bookingRepository.save(booking);
      return _booking;
    } else {
      throw new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND);
    }
  }

  @Override
  public Booking editBooking(Map<String, Object> updates, Long id, String username) {

    if (merchantRepository.existsByUsername(username)) {
      Booking booking = bookingRepository.findById(id)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.BOOKING_NOT_FOUND));

      if (!booking.getOutbound().getMerchant().getUsername().equals(username)) {
        throw new InternalException(ErrorConstant.USER_ACCESS_DENIED);
      }

      if (booking.getOutbound().getStatus().equals(EnumSupplyStatus.COMBINED.name())
          || booking.getOutbound().getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(ErrorConstant.OUTBOUND_IS_IN_TRANSACTION);
      }

      String portOfLoadingRequest = String.valueOf(updates.get("portOfLoading"));
      if (updates.get("portOfLoading") != null
          && !Tool.isEqual(booking.getPortOfLoading().getNameCode(), portOfLoadingRequest)) {
        Port portOfLoading = portRepository.findByNameCode(portOfLoadingRequest)
            .orElseThrow(() -> new NotFoundException(ErrorConstant.PORT_NOT_FOUND));
        booking.setPortOfLoading(portOfLoading);
      }

      String bookingNumberRequest = String.valueOf(updates.get("bookingNumber"));
      if (updates.get("bookingNumber") != null && !Tool.isEqual(booking.getBookingNumber(), bookingNumberRequest)) {
        if (bookingRepository.existsByBookingNumber(bookingNumberRequest)) {
          throw new DuplicateRecordException(ErrorConstant.BOOKING_ALREADY_EXISTS);
        }
        booking.setBookingNumber(bookingNumberRequest);
      }

      String unit = String.valueOf(updates.get("unit"));
      if (updates.get("unit") != null && !Tool.isEqual(booking.getUnit(), unit)) {
        booking.setUnit(Integer.parseInt(unit));
      }

      String cutOffTimeRequest = String.valueOf(updates.get("cutOffTime"));
      if (updates.get("cutOffTime") != null
          && !Tool.isEqual(String.valueOf(booking.getCutOffTime()), cutOffTimeRequest)) {
        LocalDateTime cutOffTime = Tool.convertToLocalDateTime(cutOffTimeRequest);
        booking.setCutOffTime(cutOffTime);
      }

      Boolean isFcl = (Boolean) updates.get("isFcl");
      if (updates.get("isFcl") != null && isFcl != null) {
        booking.setIsFcl(isFcl);
      }

      Booking _booking = bookingRepository.save(booking);
      return _booking;
    } else {
      throw new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND);
    }
  }

  @Override
  public Booking getBookingById(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BOOKING_NOT_FOUND));
    return booking;
  }

  @Override
  public Booking getBookingsByBookingNumber(String bookingNumber) {
    Booking booking = bookingRepository.findByBookingNumber(bookingNumber)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BOOKING_NOT_FOUND));
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
