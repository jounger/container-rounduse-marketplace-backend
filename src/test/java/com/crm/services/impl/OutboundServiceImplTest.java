package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.crm.common.Tool;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Booking;
import com.crm.models.ContainerType;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.BookingRequest;
import com.crm.payload.request.OutboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ContainerTypeRepository;
import com.crm.repository.MerchantRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.PortRepository;
import com.crm.repository.ShippingLineRepository;
import com.crm.repository.SupplyRepository;

public class OutboundServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(OutboundServiceImplTest.class);

  @InjectMocks
  OutboundServiceImpl outboundServiceImpl;

  @Mock
  private MerchantRepository merchantRepository;

  @Mock
  private OutboundRepository outboundRepository;

  @Mock
  private ShippingLineRepository shippingLineRepository;

  @Mock
  private ContainerTypeRepository containerTypeRepository;

  @Mock
  private PortRepository portRepository;

  @Mock
  private SupplyRepository supplyRepository;

  PaginationRequest paginationRequest;

  PaginationRequest paginationRequestHasStatus;

  Page<Outbound> pages;

  List<Outbound> outbounds;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    outbounds = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);

    paginationRequestHasStatus = new PaginationRequest();
    paginationRequestHasStatus.setPage(0);
    paginationRequestHasStatus.setLimit(10);
    paginationRequestHasStatus.setStatus("CREATED");
  }

  @Test
  @DisplayName("Get OutboundById success")
  public void whenGetOutboundById_thenReturnOutbound() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));

    // then
    Outbound actualResult = outboundServiceImpl.getOutboundById(outbound.getId());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getMerchant().getId()).isEqualTo(merchant.getId());
  }

  @Test
  @DisplayName("Get OutboundById Not found")
  public void whenGetOutboundById_thenReturn404() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      outboundServiceImpl.getOutboundById(2L);
    });
  }

  @Test
  @DisplayName("Get outbounds success")
  public void whenGetOutbounds_thenReturnInboundPages() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    outbounds.add(outbound);
    pages = new PageImpl<Outbound>(outbounds);

    // when
    when(outboundRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Outbound> actualPages = outboundServiceImpl.getOutbounds(paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getMerchant().getId()).isEqualTo(merchant.getId());
  }

  @Test
  @DisplayName("Get outbounds by status success")
  public void whenGetOutboundsByStatus_thenReturnInboundPages() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    outbounds.add(outbound);
    pages = new PageImpl<Outbound>(outbounds);

    // when
    when(outboundRepository.findByStatus(Mockito.anyString(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Outbound> actualPages = outboundServiceImpl.getOutbounds(paginationRequestHasStatus);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getMerchant().getId()).isEqualTo(merchant.getId());
  }

  @Test
  @DisplayName("Get outbounds By Merchant success")
  public void whenGetOutboundsByMerchant_thenReturnInboundPages() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    outbounds.add(outbound);
    pages = new PageImpl<Outbound>(outbounds);

    // when
    when(outboundRepository.findByMerchant(Mockito.anyString(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Outbound> actualPages = outboundServiceImpl.getOutboundsByMerchant(merchant.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getMerchant().getId()).isEqualTo(merchant.getId());
  }

  @Test
  @DisplayName("Get outbounds by merchant and status success")
  public void whenGetOutboundsByMerchantAndStatus_thenReturnInboundPages() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    outbounds.add(outbound);
    pages = new PageImpl<Outbound>(outbounds);

    // when
    when(outboundRepository.findByMerchant(Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Page<Outbound> actualPages = outboundServiceImpl.getOutboundsByMerchant(merchant.getUsername(),
        paginationRequestHasStatus);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getMerchant().getId()).isEqualTo(merchant.getId());
  }

  @Test
  @DisplayName("Create outbound success")
  public void whenCreateOutbound_thenReturnInbound() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setUnit(3);
    bookingRequest.setNumber("001");
    bookingRequest.setCutOffTime("2020-07-30T19:20");
    bookingRequest.setPortOfLoading(port.getNameCode());

    OutboundRequest outboundRequest = new OutboundRequest();
    outboundRequest.setCode("0002");
    outboundRequest.setShippingLine(shippingLine.getCompanyCode());
    outboundRequest.setContainerType(containerType.getName());
    outboundRequest.setGoodsDescription("Good Job");
    outboundRequest.setPackingTime("2020-07-26T15:41");
    outboundRequest.setDeliveryTime("2020-07-29T20:45");
    outboundRequest.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outboundRequest.setGrossWeight(10.5);
    outboundRequest.setUnitOfMeasurement("KG");
    outboundRequest.setStatus("CREATED");
    outboundRequest.setBooking(bookingRequest);

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Outbound actualResult = outboundServiceImpl.createOutbound(merchant.getUsername(), outboundRequest);
    assertThat(actualResult).isNotNull();
  }

  @Test
  @DisplayName("Create outbound when outbound has exists")
  public void whenCreateOutbound_thenReturnDuplicateException() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setUnit(3);
    bookingRequest.setNumber("001");
    bookingRequest.setCutOffTime("2020-07-30T19:20");
    bookingRequest.setPortOfLoading(port.getNameCode());

    OutboundRequest outboundRequest = new OutboundRequest();
    outboundRequest.setCode("0002");
    outboundRequest.setShippingLine(shippingLine.getCompanyCode());
    outboundRequest.setContainerType(containerType.getName());
    outboundRequest.setGoodsDescription("Good Job");
    outboundRequest.setPackingTime("2020-07-26T15:41");
    outboundRequest.setDeliveryTime("2020-07-29T20:45");
    outboundRequest.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outboundRequest.setGrossWeight(10.5);
    outboundRequest.setUnitOfMeasurement("KG");
    outboundRequest.setStatus("CREATED");
    outboundRequest.setBooking(bookingRequest);

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(true);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      outboundServiceImpl.createOutbound(merchant.getUsername(), outboundRequest);
    });
  }

  @Test
  @DisplayName("Create outbound when ShippingLine notFound")
  public void whenCreateOutbound_thenReturnNotFoundException_ShippingLine() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setUnit(3);
    bookingRequest.setNumber("001");
    bookingRequest.setCutOffTime("2020-07-30T19:20");
    bookingRequest.setPortOfLoading(port.getNameCode());

    OutboundRequest outboundRequest = new OutboundRequest();
    outboundRequest.setCode("0002");
    outboundRequest.setShippingLine(shippingLine.getCompanyCode());
    outboundRequest.setContainerType(containerType.getName());
    outboundRequest.setGoodsDescription("Good Job");
    outboundRequest.setPackingTime("2020-07-26T15:41");
    outboundRequest.setDeliveryTime("2020-07-29T20:45");
    outboundRequest.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outboundRequest.setGrossWeight(10.5);
    outboundRequest.setUnitOfMeasurement("KG");
    outboundRequest.setStatus("CREATED");
    outboundRequest.setBooking(bookingRequest);

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.empty());
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      outboundServiceImpl.createOutbound(merchant.getUsername(), outboundRequest);
    });
  }

  @Test
  @DisplayName("Create outbound when ContainerType notFound")
  public void whenCreateOutbound_thenReturnNotFoundException_ContainerType() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setUnit(3);
    bookingRequest.setNumber("001");
    bookingRequest.setCutOffTime("2020-07-30T19:20");
    bookingRequest.setPortOfLoading(port.getNameCode());

    OutboundRequest outboundRequest = new OutboundRequest();
    outboundRequest.setCode("0002");
    outboundRequest.setShippingLine(shippingLine.getCompanyCode());
    outboundRequest.setContainerType(containerType.getName());
    outboundRequest.setGoodsDescription("Good Job");
    outboundRequest.setPackingTime("2020-07-26T15:41");
    outboundRequest.setDeliveryTime("2020-07-29T20:45");
    outboundRequest.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outboundRequest.setGrossWeight(10.5);
    outboundRequest.setUnitOfMeasurement("KG");
    outboundRequest.setStatus("CREATED");
    outboundRequest.setBooking(bookingRequest);

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      outboundServiceImpl.createOutbound(merchant.getUsername(), outboundRequest);
    });
  }

  @Test
  @DisplayName("Create outbound when ContainerType notFound")
  public void whenCreateOutbound_thenReturnNotFoundException_Port() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setUnit(3);
    bookingRequest.setNumber("001");
    bookingRequest.setCutOffTime("2020-07-30T19:20");
    bookingRequest.setPortOfLoading(port.getNameCode());

    OutboundRequest outboundRequest = new OutboundRequest();
    outboundRequest.setCode("0002");
    outboundRequest.setShippingLine(shippingLine.getCompanyCode());
    outboundRequest.setContainerType(containerType.getName());
    outboundRequest.setGoodsDescription("Good Job");
    outboundRequest.setPackingTime("2020-07-26T15:41");
    outboundRequest.setDeliveryTime("2020-07-29T20:45");
    outboundRequest.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outboundRequest.setGrossWeight(10.5);
    outboundRequest.setUnitOfMeasurement("KG");
    outboundRequest.setStatus("CREATED");
    outboundRequest.setBooking(bookingRequest);

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.empty());
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      outboundServiceImpl.createOutbound(merchant.getUsername(), outboundRequest);
    });
  }

  @Test
  @DisplayName("Create outbound when Booking notFound")
  public void whenCreateOutbound_thenReturnNotFoundException_Booking() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setUnit(3);
    bookingRequest.setCutOffTime("2020-07-30T19:20");
    bookingRequest.setPortOfLoading(port.getNameCode());

    OutboundRequest outboundRequest = new OutboundRequest();
    outboundRequest.setCode("0002");
    outboundRequest.setShippingLine(shippingLine.getCompanyCode());
    outboundRequest.setContainerType(containerType.getName());
    outboundRequest.setGoodsDescription("Good Job");
    outboundRequest.setPackingTime("2020-07-26T15:41");
    outboundRequest.setDeliveryTime("2020-07-29T20:45");
    outboundRequest.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outboundRequest.setGrossWeight(10.5);
    outboundRequest.setUnitOfMeasurement("KG");
    outboundRequest.setStatus("CREATED");
    outboundRequest.setBooking(bookingRequest);

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      outboundServiceImpl.createOutbound(merchant.getUsername(), outboundRequest);
    });
  }

  @Test
  @DisplayName("Create outbound when packingTime after deliveryTime")
  public void whenCreateOutbound_thenPackingTimeAfterDeliveryTime() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setUnit(3);
    bookingRequest.setNumber("001");
    bookingRequest.setCutOffTime("2020-07-30T19:20");
    bookingRequest.setPortOfLoading(port.getNameCode());

    OutboundRequest outboundRequest = new OutboundRequest();
    outboundRequest.setCode("0002");
    outboundRequest.setShippingLine(shippingLine.getCompanyCode());
    outboundRequest.setContainerType(containerType.getName());
    outboundRequest.setGoodsDescription("Good Job");
    outboundRequest.setPackingTime("2020-07-30T15:41");
    outboundRequest.setDeliveryTime("2020-07-29T20:45");
    outboundRequest.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outboundRequest.setGrossWeight(10.5);
    outboundRequest.setUnitOfMeasurement("KG");
    outboundRequest.setStatus("CREATED");
    outboundRequest.setBooking(bookingRequest);

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      outboundServiceImpl.createOutbound(merchant.getUsername(), outboundRequest);
    });
  }

  @Test
  @DisplayName("Edit Outbound success")
  public void whenEditOutbound_thenReturnOutbound() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("packingTime", "2020-07-25T15:41");
    updates.put("deliveryTime", "2020-07-28T15:41");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Outbound actualResult = outboundServiceImpl.editOutbound(updates, outbound.getId(), merchant.getUsername());
    assertThat(actualResult).isNotNull();
  }

  @Test
  @DisplayName("Edit Outbound NotFound")
  public void whenEditOutbound_thenReturnNotFoundException_Outbound() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("packingTime", "2020-07-25T15:41");
    updates.put("deliveryTime", "2020-07-28T15:41");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      outboundServiceImpl.editOutbound(updates, outbound.getId(), merchant.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Outbound ShippingLine NotFound")
  public void whenEditOutbound_thenReturnNotFoundException_ShippingLine() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("packingTime", "2020-07-25T15:41");
    updates.put("deliveryTime", "2020-07-28T15:41");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.empty());
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      outboundServiceImpl.editOutbound(updates, outbound.getId(), merchant.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Outbound ContainerType NotFound")
  public void whenEditOutbound_thenReturnNotFoundException_ContainerType() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("packingTime", "2020-07-25T15:41");
    updates.put("deliveryTime", "2020-07-28T15:41");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      outboundServiceImpl.editOutbound(updates, outbound.getId(), merchant.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Outbound Access Denied")
  public void whenEditOutbound_thenReturnAccessDeniedException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("packingTime", "2020-07-25T15:41");
    updates.put("deliveryTime", "2020-07-28T15:41");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      outboundServiceImpl.editOutbound(updates, outbound.getId(), "XXXXX");
    });
  }

  @Test
  @DisplayName("Edit Outbound COMBINED or BIDDING")
  public void whenEditOutbound_thenReturnOutboundIsInTransactionException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("packingTime", "2020-07-25T15:41");
    updates.put("deliveryTime", "2020-07-28T15:41");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("COMBINED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      outboundServiceImpl.editOutbound(updates, outbound.getId(), merchant.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Outbound when packingTime after deliveryTime")
  public void whenEditOutbound_thenReturnOutboundInvalidDeliveryException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("packingTime", "2020-07-28T15:41");
    updates.put("deliveryTime", "2020-07-25T15:41");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(outbound);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      outboundServiceImpl.editOutbound(updates, outbound.getId(), merchant.getUsername());
    });
  }

  @Test
  @DisplayName("Remove Outbound Success")
  public void whenRemoveOutboundSuccess() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    // then
    outboundServiceImpl.removeOutbound(outbound.getId(), merchant.getUsername());
  }

  @Test
  @DisplayName("Remove Outbound NotFound")
  public void whenRemoveOutboundNotFound() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      outboundServiceImpl.removeOutbound(outbound.getId(), merchant.getUsername());
    });
  }

  @Test
  @DisplayName("Remove Outbound AccessDenied")
  public void whenRemoveOutbound_whenAccessDenied() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("CREATED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      outboundServiceImpl.removeOutbound(outbound.getId(), "XXXXX");
    });
  }

  @Test
  @DisplayName("Remove Outbound Combined or Bidding")
  public void whenRemoveOutbound_whenOutboundIsInTransaction() {
    // given

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setCode("0002");
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setMerchant(merchant);
    outbound.setGoodsDescription("Good Job");
    outbound.setPackingTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    outbound.setDeliveryTime(Tool.convertToLocalDateTime("2020-07-29T20:45"));
    outbound.setPackingStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    outbound.setGrossWeight(10.5);
    outbound.setUnitOfMeasurement("KG");
    outbound.setStatus("COMBINED");
    outbound.setBooking(booking);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      outboundServiceImpl.removeOutbound(outbound.getId(), merchant.getUsername());
    });
  }
}
