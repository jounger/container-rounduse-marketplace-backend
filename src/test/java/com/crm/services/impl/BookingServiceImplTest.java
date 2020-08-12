package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import com.crm.exception.NotFoundException;
import com.crm.models.Booking;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BookingRepository;
import com.crm.repository.MerchantRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.PortRepository;

public class BookingServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(BookingServiceImplTest.class);

  @InjectMocks
  BookingServiceImpl bookingServiceImpl;

  @Mock
  BookingRepository bookingRepository;

  @Mock
  MerchantRepository merchantRepository;

  @Mock
  private PortRepository portRepository;

  @Mock
  private OutboundRepository outboundRepository;

  PaginationRequest paginationRequest;

  PaginationRequest paginationRequestHasStatus;

  Page<Booking> pages;

  List<Booking> bookings;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    bookings = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);

    paginationRequestHasStatus = new PaginationRequest();
    paginationRequestHasStatus.setPage(0);
    paginationRequestHasStatus.setLimit(10);
    paginationRequestHasStatus.setStatus("CREATED");
  }

  @Test
  @DisplayName("Get BookingByOutbound success")
  public void whengetBookingByOutbound_thenReturnBooking() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setMerchant(merchant);

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);
    booking.setOutbound(outbound);

    // when
    when(outboundRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(bookingRepository.findByOutbound(Mockito.anyLong())).thenReturn(Optional.of(booking));

    // then
    Booking actualResult = bookingServiceImpl.getBookingByOutbound(outbound.getId());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getOutbound().getMerchant().getId()).isEqualTo(merchant.getId());
  }

  @Test
  @DisplayName("Get BookingByOutbound when outbound not found")
  public void whengetBookingByOutbound_thenReturnNotFoundException_Outbound() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setMerchant(merchant);

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);
    booking.setOutbound(outbound);

    // when
    when(outboundRepository.existsById(Mockito.anyLong())).thenReturn(false);
    when(bookingRepository.findByOutbound(Mockito.anyLong())).thenReturn(Optional.of(booking));

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bookingServiceImpl.getBookingByOutbound(2L);
    });
  }

  @Test
  @DisplayName("Get BookingByOutbound when booking not found")
  public void whengetBookingByOutbound_thenReturnNotFoundException_Booking() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setMerchant(merchant);

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);
    booking.setOutbound(outbound);

    // when
    when(outboundRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(bookingRepository.findByOutbound(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bookingServiceImpl.getBookingByOutbound(outbound.getId());
    });
  }

  @Test
  @DisplayName("Get BookingById success")
  public void whengetBookingById_thenReturnBooking() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setMerchant(merchant);

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);
    booking.setOutbound(outbound);

    // when
    when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

    // then
    Booking actualResult = bookingServiceImpl.getBookingById(booking.getId());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getOutbound().getMerchant().getId()).isEqualTo(merchant.getId());
  }

  @Test
  @DisplayName("Get BookingById when Booking not found")
  public void whengetBookingById_thenReturnNotFoundException_Booking() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setMerchant(merchant);

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);
    booking.setOutbound(outbound);

    // when
    when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bookingServiceImpl.getBookingById(2L);
    });
  }

  @Test
  @DisplayName("Get BookingByNumber success")
  public void whengetBookingByNumber_thenReturnBooking() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setMerchant(merchant);

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);
    booking.setNumber("22L");
    booking.setOutbound(outbound);

    // when
    when(bookingRepository.findByNumber(Mockito.anyString())).thenReturn(Optional.of(booking));

    // then
    Booking actualResult = bookingServiceImpl.getBookingsByNumber(booking.getNumber());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getOutbound().getMerchant().getId()).isEqualTo(merchant.getId());
  }

  @Test
  @DisplayName("Get BookingByNumber when Booking not found")
  public void whengetBookingByNumber_thenReturnNotFoundException_Booking() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setMerchant(merchant);

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);
    booking.setOutbound(outbound);

    // when
    when(bookingRepository.findByNumber(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bookingServiceImpl.getBookingsByNumber("XX2L");
    });
  }
}
