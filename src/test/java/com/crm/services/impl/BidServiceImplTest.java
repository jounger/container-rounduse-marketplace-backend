package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BiddingNotification;
import com.crm.models.BillOfLading;
import com.crm.models.Booking;
import com.crm.models.Container;
import com.crm.models.ContainerType;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.Role;
import com.crm.models.ShippingLine;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.BiddingNotificationRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;

public class BidServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(BidServiceImplTest.class);

  @InjectMocks
  BidServiceImpl bidServiceImpl;

  @Mock
  private BidRepository bidRepository;

  @Mock
  private BiddingDocumentRepository biddingDocumentRepository;

  @Mock
  private ForwarderRepository forwarderRepository;

  @Mock
  private ContainerRepository containerRepository;

  @Mock
  private OutboundRepository outboundRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private SupplierRepository supplierRepository;

  @Mock
  private BiddingNotificationRepository biddingNotificationRepository;

  PaginationRequest paginationRequest;

  PaginationRequest paginationRequestHasStatus;

  List<Bid> bids;

  Page<Bid> pages;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    bids = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);

    paginationRequestHasStatus = new PaginationRequest();
    paginationRequestHasStatus.setPage(0);
    paginationRequestHasStatus.setLimit(10);
    paginationRequestHasStatus.setStatus("PENDING");
  }

  @Test
  @DisplayName("Create bid success")
  public void whenCreateBid_thenReturnBid() {
    // given
    List<Long> containers = new ArrayList<>();
    containers.add(1L);

    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidder("merchant");
    request.setBidPrice(1000D);
    request.setContainers(containers);
    request.setValidityPeriod(Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(1)));

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setFreeTime(LocalDateTime.now().plusDays(1));

    Container container = new Container();
    container.setId(1L);
    container.setBillOfLading(billOfLading);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);

    BiddingNotification biddingNotification = new BiddingNotification();
    biddingNotification.setId(1L);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(containerRepository.existsByOutbound(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(null);
    when(bidRepository.save(Mockito.any(Bid.class))).thenReturn(bid);
    when(biddingDocumentRepository.save(Mockito.any(BiddingDocument.class))).thenReturn(null);
    when(biddingNotificationRepository.findByUserAndBiddingDocument(Mockito.anyString(), Mockito.anyLong()))
        .thenReturn(Optional.of(biddingNotification));
    when(biddingNotificationRepository.save(Mockito.any(BiddingNotification.class))).thenReturn(null);

    // then
    Bid actualResult = bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(bid.getId());
    assertThat(actualResult.getBidPrice()).isEqualTo(bid.getBidPrice());
    logger.info("Response: {}", actualResult.getBidPrice());
  }

  @Test
  @DisplayName("Create bid when BiddingDocument notfound")
  public void whenCreateBid_thenReturnBiddingDocument404() {
    // given
    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(Arrays.asList(1L));

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when bidding document status is CANCELED")
  public void whenCreateBid_thenThrowBiddingDocumentIsTimeoutException_CANCELED() {
    // given
    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(Arrays.asList(1L));

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("CANCELED");

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when bidding document status is EXPIRED")
  public void whenCreateBid_thenThrowBiddingDocumentIsTimeoutException_EXPIRED() {
    // given
    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(Arrays.asList(1L));

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("EXPIRED");

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when bid closing before now")
  public void whenCreateBid_thenThrowBiddingDocumentIsTimeoutException_BidClosingBeforeNow() {
    // given
    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(Arrays.asList(1L));

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBidClosing(LocalDateTime.now().minusDays(1));

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when Forwarder Not found")
  public void whenCreateBid_thenReturnForwarder404() {
    // given
    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(Arrays.asList(1L));

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when duplicate forwarder in one bidding document")
  public void whenCreateBid_thenReturnDuplicate400() {
    // given
    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(Arrays.asList(1L));

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setBids(Arrays.asList(bid));

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when Container more than needed")
  public void whenCreateBid_thenReturnContainerMoreThanNeededException() {
    // given
    List<Long> containers = new ArrayList<>();
    containers.add(1L);
    containers.add(2L);

    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(containers);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Forwarder bidder1 = new Forwarder();
    bidder1.setId(2L);
    bidder1.setUsername("forwarder1");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder1);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setBids(Arrays.asList(bid));
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when container less than needed")
  public void whenCreateBid_thenReturnContainerLessThanNeededException() {
    // given
    List<Long> containers = new ArrayList<>();
    containers.add(1L);
    containers.add(2L);

    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(containers);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Forwarder bidder1 = new Forwarder();
    bidder1.setId(2L);
    bidder1.setUsername("forwarder1");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder1);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(5);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setBids(Arrays.asList(bid));
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(false);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when container not found")
  public void whenCreateBid_thenReturnContainerNotFoundException() {
    // given
    List<Long> containers = new ArrayList<>();
    containers.add(1L);

    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(containers);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Forwarder bidder1 = new Forwarder();
    bidder1.setId(2L);
    bidder1.setUsername("forwarder1");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder1);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(5);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setBids(Arrays.asList(bid));
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when freeTime before now")
  public void whenCreateBid_thenReturnInboundInvalidFreeTimeException() {
    // given
    List<Long> containers = new ArrayList<>();
    containers.add(1L);

    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(containers);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Forwarder bidder1 = new Forwarder();
    bidder1.setId(2L);
    bidder1.setUsername("forwarder1");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder1);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(5);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setBids(Arrays.asList(bid));
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setFreeTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);
    container.setBillOfLading(billOfLading);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when container not suitable")
  public void whenCreateBid_thenReturnContainerNotSuitableException() {
    // given
    List<Long> containers = new ArrayList<>();
    containers.add(1L);

    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(containers);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Forwarder bidder1 = new Forwarder();
    bidder1.setId(2L);
    bidder1.setUsername("forwarder1");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder1);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(5);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setBids(Arrays.asList(bid));
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setFreeTime(LocalDateTime.now().plusDays(1));

    Container container = new Container();
    container.setId(1L);
    container.setBillOfLading(billOfLading);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(containerRepository.existsByOutbound(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when bidPrice less than bidFloorPrice")
  public void whenCreateBid_thenReturnBidInvalidBidPriceException() {
    // given
    List<Long> containers = new ArrayList<>();
    containers.add(1L);

    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(containers);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Forwarder bidder1 = new Forwarder();
    bidder1.setId(2L);
    bidder1.setUsername("forwarder1");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder1);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(5);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setBids(Arrays.asList(bid));
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);
    biddingDocument.setBidFloorPrice(2000D);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setFreeTime(LocalDateTime.now().plusDays(1));

    Container container = new Container();
    container.setId(1L);
    container.setBillOfLading(billOfLading);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(containerRepository.existsByOutbound(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create bid when validityPeriod before now")
  public void whenCreateBid_thenReturnBidInvalidValidityPeriodException() {
    // given
    List<Long> containers = new ArrayList<>();
    containers.add(1L);

    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(containers);
    request.setValidityPeriod(Tool.convertLocalDateTimeToString(LocalDateTime.now().minusDays(1)));

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Forwarder bidder1 = new Forwarder();
    bidder1.setId(2L);
    bidder1.setUsername("forwarder1");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder1);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(5);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setBids(Arrays.asList(bid));
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);
    biddingDocument.setBidFloorPrice(900D);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setFreeTime(LocalDateTime.now().plusDays(1));

    Container container = new Container();
    container.setId(1L);
    container.setBillOfLading(billOfLading);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(containerRepository.existsByOutbound(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Get bid Success")
  public void whenGetBid_thenReturnBid() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setBidClosing(LocalDateTime.now().minusDays(2));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);
    bid.setBiddingDocument(biddingDocument);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));

    // then
    Bid actualResult = bidServiceImpl.getBid(bid.getId(), bidder.getUsername());
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getBiddingDocument()).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getBidder().getId()).isEqualTo(bidder.getId());
  }

  @Test
  @DisplayName("Get when bid not found")
  public void whenGetBid_thenReturnBid404() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.getBid(1L, bidder.getUsername());
    });
  }

  @Test
  @DisplayName("Get when Access Denied")
  public void whenGetBid_thenReturnAccessDeniedException() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setBidClosing(LocalDateTime.now().minusDays(1));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);
    bid.setBiddingDocument(biddingDocument);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      bidServiceImpl.getBid(1L, "XXXX");
    });
  }

  @Test
  @DisplayName("Get bids By BiddingDocument")
  public void whenGetBidsByBiddingDocument_thenReturnBid() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setBidClosing(LocalDateTime.now().minusDays(2));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);
    bid.setBiddingDocument(biddingDocument);

    bids.add(bid);
    pages = new PageImpl<Bid>(bids);

    // when
    when(biddingDocumentRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(supplierRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(bidRepository.findByBiddingDocument(Mockito.anyLong(), Mockito.anyString(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Page<Bid> actualPages = bidServiceImpl.getBidsByBiddingDocument(biddingDocument.getId(), bidder.getUsername(),
        paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getBidder().getId()).isEqualTo(bidder.getId());
  }

  @Test
  @DisplayName("Get bids By BiddingDocument and status")
  public void whenGetBidsByBiddingDocumentAndStatus_thenReturnBid() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setBidClosing(LocalDateTime.now().minusDays(2));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);
    bid.setBiddingDocument(biddingDocument);
    bid.setStatus("PENDING");

    bids.add(bid);
    pages = new PageImpl<Bid>(bids);

    // when
    when(biddingDocumentRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(supplierRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(bidRepository.findByBiddingDocument(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Bid> actualPages = bidServiceImpl.getBidsByBiddingDocument(biddingDocument.getId(), bidder.getUsername(),
        paginationRequestHasStatus);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getBidder().getId()).isEqualTo(bidder.getId());
  }

  @Test
  @DisplayName("Get bids By BiddingDocument when BiddingDocument notfound")
  public void whenGetBidsByBiddingDocumentAndStatus_thenReturn404_BiddingDocument() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setBidClosing(LocalDateTime.now().minusDays(2));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);
    bid.setBiddingDocument(biddingDocument);
    bid.setStatus("PENDING");

    bids.add(bid);
    pages = new PageImpl<Bid>(bids);

    // when
    when(biddingDocumentRepository.existsById(Mockito.anyLong())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.getBidsByBiddingDocument(biddingDocument.getId(), bidder.getUsername(),
          paginationRequestHasStatus);
    });
  }

  @Test
  @DisplayName("Get bids By BiddingDocument when Supplier notfound")
  public void whenGetBidsByBiddingDocumentAndStatus_thenReturn404_Supplier() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setBidClosing(LocalDateTime.now().minusDays(2));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);
    bid.setBiddingDocument(biddingDocument);
    bid.setStatus("PENDING");

    bids.add(bid);
    pages = new PageImpl<Bid>(bids);

    // when
    when(biddingDocumentRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(supplierRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.getBidsByBiddingDocument(biddingDocument.getId(), bidder.getUsername(),
          paginationRequestHasStatus);
    });
  }

  @Test
  @DisplayName("Get bids By BiddingDocument And Exist Combined Success")
  public void whenGetBidsByBiddingDocumentAndExistCombined_thenReturnBids() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBids(Arrays.asList(bid));

    bids.add(bid);
    pages = new PageImpl<Bid>(bids);

    // when
    when(biddingDocumentRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(supplierRepository.existsByUsername(Mockito.anyString())).thenReturn(true);
    when(bidRepository.findByBiddingDocumentAndExistCombined(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Bid> actualPages = bidServiceImpl.getBidsByBiddingDocumentAndExistCombined(biddingDocument.getId(),
        bidder.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getBidder().getId()).isEqualTo(bidder.getId());
  }

  @Test
  @DisplayName("Get bids By BiddingDocument And Exist Combined when BiddingDocument notfound")
  public void whenGetBidsByBiddingDocumentAndExistCombined_thenReturnBiddingDocument404() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBids(Arrays.asList(bid));

    // when
    when(biddingDocumentRepository.existsById(Mockito.anyLong())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.getBidsByBiddingDocumentAndExistCombined(biddingDocument.getId(), bidder.getUsername(),
          paginationRequest);
    });
  }

  @Test
  @DisplayName("Get bids By BiddingDocument And Exist Combined when Supplier notfound")
  public void whenGetBidsByBiddingDocumentAndExistCombined_thenReturnSupplier404() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBids(Arrays.asList(bid));

    // when
    when(biddingDocumentRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(supplierRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.getBidsByBiddingDocumentAndExistCombined(biddingDocument.getId(), bidder.getUsername(),
          paginationRequest);
    });
  }

  @Test
  @DisplayName("Get bids By Forwarder")
  public void whenGetBidsByForwarder_thenReturnBids() {
    // given

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setBids(Arrays.asList(bid));

    bids.add(bid);
    pages = new PageImpl<Bid>(bids);

    // when
    when(bidRepository.findByForwarder(Mockito.anyString(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Bid> actualPages = bidServiceImpl.getBidsByForwarder(bidder.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getBidder().getId()).isEqualTo(bidder.getId());
  }

  @Test
  @DisplayName("Get bids By Forwarder And Status")
  public void whenGetBidsByForwarderAndStatus_thenReturnBids() {
    // given
    paginationRequest.setStatus(EnumBidStatus.PENDING.name());

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);
    bid.setStatus(EnumBidStatus.PENDING.name());

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setBids(Arrays.asList(bid));

    bids.add(bid);
    pages = new PageImpl<Bid>(bids);

    // when
    when(bidRepository.findByForwarder(Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Page<Bid> actualPages = bidServiceImpl.getBidsByForwarder(bidder.getUsername(), paginationRequestHasStatus);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getStatus()).isEqualTo(paginationRequest.getStatus());
    assertThat(actualPages.getContent().get(0).getBidder().getId()).isEqualTo(bidder.getId());
  }

  @Test
  @DisplayName("Edit bids Success")
  public void whenEditBid_thenReturnBid() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "REJECTED");
    updates.put("bidPrice", 2000);
//    updates.put("validityPeriod", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(5)));

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");
    bidder.setRoles(roles);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(null);
    when(bidRepository.save(Mockito.any(Bid.class))).thenReturn(bid);

    // then
    Bid actualResult = bidServiceImpl.editBid(bid.getId(), bidder.getUsername(), updates);
    assertThat(actualResult).isNotNull();
    logger.info("Response: {}", actualResult.getBidPrice());
  }

  @Test
  @DisplayName("Edit bids Success when bid Cancel")
  public void whenEditBid_thenReturnBid_status_CANCELED() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "CANCELED");
    updates.put("bidPrice", 2000);
//    updates.put("validityPeriod", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(5)));

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");
    bidder.setRoles(roles);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(null);
    when(bidRepository.save(Mockito.any(Bid.class))).thenReturn(bid);

    // then
    Bid actualResult = bidServiceImpl.editBid(bid.getId(), bidder.getUsername(), updates);
    assertThat(actualResult).isNotNull();
    logger.info("Response: {}", actualResult.getBidPrice());
  }

  @Test
  @DisplayName("Edit bids when bid notfound")
  public void whenEditBid_thenReturn404_Bid() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "PENDING");
    updates.put("bidPrice", 1000);
//    updates.put("validityPeriod", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(5)));

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");
    bidder.setRoles(roles);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.editBid(bid.getId(), bidder.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit bids when user notfound")
  public void whenEditBid_thenReturn404_User() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "PENDING");
    updates.put("bidPrice", 1000);
//    updates.put("validityPeriod", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(5)));

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");
    bidder.setRoles(roles);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.editBid(bid.getId(), bidder.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit bids when can not edit bid")
  public void whenEditBid_thenReturnBidInvalidEdit() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "PENDING");
    updates.put("bidPrice", 1000);
//    updates.put("validityPeriod", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(5)));

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");
    bidder.setRoles(roles);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.ACCEPTED.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.editBid(bid.getId(), bidder.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit bids when timeout")
  public void whenEditBid_thenReturnBiddingDoucumentTimeOutException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "PENDING");
    updates.put("bidPrice", 1000);
//    updates.put("validityPeriod", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(5)));

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");
    bidder.setRoles(roles);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("CANCELED");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.editBid(bid.getId(), bidder.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit bids when validityPeriod before now")
  public void whenEditBid_thenReturnValidityPeriodBeforeNowException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "PENDING");
    updates.put("bidPrice", 1000);
    updates.put("validityPeriod", Tool.convertLocalDateTimeToString(LocalDateTime.now().minusDays(5)));

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");
    bidder.setRoles(roles);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.editBid(bid.getId(), bidder.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit bids when container Busy")
  public void whenEditBid_thenReturnContainerBusyException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "PENDING");
    updates.put("bidPrice", 1000);
    updates.put("validityPeriod", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(15)));

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");
    bidder.setRoles(roles);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.BIDDING.name());

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    List<Container> containers = new ArrayList<>();
    containers.add(container);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.editBid(bid.getId(), bidder.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit bids when freezeTime after now")
  public void whenEditBid_thenReturnFreezeTimeAfterNowException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "PENDING");
    updates.put("bidPrice", 1000);
    updates.put("validityPeriod", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(5)));

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");
    bidder.setRoles(roles);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    List<Container> containers = new ArrayList<>();
    containers.add(container);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(null);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.editBid(bid.getId(), bidder.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit bids when combined Success")
  public void whenEditBidWhenCombined_thenReturnBid() {
    // given

    List<Long> containerId = new ArrayList<>();
    containerId.add(1L);

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.setRoles(roles);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(2);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Bid bid1 = new Bid();
    bid1.setId(2L);
    bid1.setBidPrice(1000D);
    bid1.setStatus(EnumBidStatus.PENDING.name());

    bids.add(bid1);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));
    biddingDocument.setBids(bids);

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    containers.add(container1);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(containerRepository.countCombinedContainersByBiddingDocument(Mockito.anyLong()))
        .thenReturn(Long.valueOf(booking.getUnit() - containerId.size()));
    when(biddingDocumentRepository.save(Mockito.any(BiddingDocument.class))).thenReturn(null);
    when(outboundRepository.save(Mockito.any(Outbound.class))).thenReturn(null);
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(null);
    when(bidRepository.save(Mockito.any(Bid.class))).thenReturn(bid);

    // then
    Bid actualResult = bidServiceImpl.editBidWhenCombined(bid.getId(), merchant.getUsername(), containerId);
    assertThat(actualResult).isNotNull();
    logger.info("Response: {}", actualResult.getBidPrice());
  }

  @Test
  @DisplayName("Edit bids when combined when bid notfound")
  public void whenEditBidWhenCombined_thenReturn404_Bid() {
    // given

    List<Long> containerId = new ArrayList<>();
    containerId.add(1L);

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.setRoles(roles);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(2);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Bid bid1 = new Bid();
    bid1.setId(2L);
    bid1.setBidPrice(1000D);
    bid1.setStatus(EnumBidStatus.PENDING.name());

    bids.add(bid1);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));
    biddingDocument.setBids(bids);

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    containers.add(container1);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.editBidWhenCombined(bid.getId(), merchant.getUsername(), containerId);
    });
  }

  @Test
  @DisplayName("Edit bids when combined when user notfound")
  public void whenEditBidWhenCombined_thenReturn404_User() {
    // given

    List<Long> containerId = new ArrayList<>();
    containerId.add(1L);

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.setRoles(roles);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(2);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Bid bid1 = new Bid();
    bid1.setId(2L);
    bid1.setBidPrice(1000D);
    bid1.setStatus(EnumBidStatus.PENDING.name());

    bids.add(bid1);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));
    biddingDocument.setBids(bids);

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    containers.add(container1);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.editBidWhenCombined(bid.getId(), merchant.getUsername(), containerId);
    });
  }

  @Test
  @DisplayName("Edit bids when combined when Access Denied")
  public void whenEditBidWhenCombined_thenReturnAccessDeniedException() {
    // given

    List<Long> containerId = new ArrayList<>();
    containerId.add(1L);

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.setRoles(roles);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(2);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Bid bid1 = new Bid();
    bid1.setId(2L);
    bid1.setBidPrice(1000D);
    bid1.setStatus(EnumBidStatus.PENDING.name());

    bids.add(bid1);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));
    biddingDocument.setBids(bids);

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    containers.add(container1);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      bidServiceImpl.editBidWhenCombined(bid.getId(), merchant.getUsername(), containerId);
    });
  }

  @Test
  @DisplayName("Edit bids when combined when Container Notfound")
  public void whenEditBidWhenCombined_thenReturn404_Container() {
    // given

    List<Long> containerId = new ArrayList<>();

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.setRoles(roles);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(2);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Bid bid1 = new Bid();
    bid1.setId(2L);
    bid1.setBidPrice(1000D);
    bid1.setStatus(EnumBidStatus.PENDING.name());

    bids.add(bid1);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));
    biddingDocument.setBids(bids);

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    containers.add(container1);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(containerRepository.countCombinedContainersByBiddingDocument(Mockito.anyLong()))
        .thenReturn(Long.valueOf(booking.getUnit() - containerId.size()));

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.editBidWhenCombined(bid.getId(), merchant.getUsername(), containerId);
    });
  }

  @Test
  @DisplayName("Edit bids when combined when Container more than need")
  public void whenEditBidWhenCombined_thenReturnContainerMoreThanNeed() {
    // given

    List<Long> containerId = new ArrayList<>();
    containerId.add(1L);

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.setRoles(roles);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(1);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Bid bid1 = new Bid();
    bid1.setId(2L);
    bid1.setBidPrice(1000D);
    bid1.setStatus(EnumBidStatus.PENDING.name());

    bids.add(bid1);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));
    biddingDocument.setBids(bids);

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    containers.add(container1);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(containerRepository.countCombinedContainersByBiddingDocument(Mockito.anyLong())).thenReturn(1L);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      bidServiceImpl.editBidWhenCombined(bid.getId(), merchant.getUsername(), containerId);
    });
  }

  @Test
  @DisplayName("Remove bid Success")
  public void whenRemoveBid_Success() {
    // given

    List<Long> containerId = new ArrayList<>();
    containerId.add(1L);

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.setRoles(roles);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(2);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    containers.add(container1);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);
    bid.setBidder(bidder);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(null);

    // then
    bidServiceImpl.removeBid(bid.getId(), bidder.getUsername());
  }

  @Test
  @DisplayName("Remove bid when bid notfound")
  public void whenRemoveBid_Return404_Bid() {
    // given

    List<Long> containerId = new ArrayList<>();
    containerId.add(1L);

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.setRoles(roles);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(2);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    containers.add(container1);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);
    bid.setBidder(bidder);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(null);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.removeBid(bid.getId(), bidder.getUsername());
    });
  }

  @Test
  @DisplayName("Remove bid when AccessDenied")
  public void whenRemoveBid_ReturnAccessDeniedException() {
    // given

    List<Long> containerId = new ArrayList<>();
    containerId.add(1L);

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.setRoles(roles);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(2);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    containers.add(container1);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);
    bid.setBidder(bidder);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(null);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.removeBid(bid.getId(), merchant.getUsername());
    });
  }
  
  @Test
  @DisplayName("Remove bid when bid Accepted")
  public void whenRemoveBid_ReturnBidAcceptedException() {
    // given

    List<Long> containerId = new ArrayList<>();
    containerId.add(1L);

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");
    roles.add(role);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.setRoles(roles);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BL00001");
    booking.setUnit(2);
    booking.setCutOffTime(LocalDateTime.now().plusDays(5));
    booking.setPortOfLoading(port);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("40HC");

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);
    outbound.setPackingTime(LocalDateTime.now().minusDays(1));

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setStatus("BIDDING");
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setPriceLeadership(105D);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(10));

    List<Container> containers = new ArrayList<Container>();
    containers.add(container);
    containers.add(container1);
    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.ACCEPTED.name());
    bid.setValidityPeriod(LocalDateTime.now().minusDays(30));
    bid.setBiddingDocument(biddingDocument);
    bid.setFreezeTime(LocalDateTime.now().minusDays(1));
    bid.setContainers(containers);
    bid.setBidder(bidder);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(null);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      bidServiceImpl.removeBid(bid.getId(), bidder.getUsername());
    });
  }
}
