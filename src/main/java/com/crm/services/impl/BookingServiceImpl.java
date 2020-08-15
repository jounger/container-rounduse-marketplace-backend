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
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Booking;
import com.crm.models.Port;
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
  public Booking getBookingByOutbound(Long id) {
    if (!outboundRepository.existsById(id)) {
      throw new NotFoundException(ErrorMessage.OUTBOUND_NOT_FOUND);
    }
    Booking booking = bookingRepository.findByOutbound(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BOOKING_NOT_FOUND));
    return booking;

  }

  @Override
  public Booking editBooking(Map<String, Object> updates, Long id, String username) {

    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BOOKING_NOT_FOUND));

    if (!booking.getOutbound().getMerchant().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    if (booking.getOutbound().getStatus().equals(EnumSupplyStatus.COMBINED.name())
        || booking.getOutbound().getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
      throw new InternalException(ErrorMessage.OUTBOUND_IS_IN_TRANSACTION);
    }

    String portOfLoadingRequest = String.valueOf(updates.get("portOfLoading"));
    if (updates.get("portOfLoading") != null
        && !Tool.isEqual(booking.getPortOfLoading().getNameCode(), portOfLoadingRequest)) {
      Port portOfLoading = portRepository.findByNameCode(portOfLoadingRequest)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.PORT_NOT_FOUND));
      booking.setPortOfLoading(portOfLoading);
    }

    String numberRequest = String.valueOf(updates.get("number"));
    if (updates.get("number") != null && !Tool.isEqual(booking.getNumber(), numberRequest)) {
      booking.setNumber(numberRequest);
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

  }

  @Override
  public Booking getBookingById(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BOOKING_NOT_FOUND));
    return booking;
  }

  @Override
  public Booking getBookingsByNumber(String number) {
    Booking booking = bookingRepository.findByNumber(number)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BOOKING_NOT_FOUND));
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
