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
import org.junit.jupiter.api.Disabled;
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

import com.crm.enums.EnumBidStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
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

  PaginationRequest paginationRequest;

  List<Bid> bids;

  Page<Bid> pages;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    bids = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
  public void whenCreateBid_thenReturnBid() {
    // given
    BidRequest request = new BidRequest();
    request.setId(1L);
    request.setBidPrice(1000D);
    request.setContainers(Arrays.asList(1L));

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
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(containerRepository.existsByOutbound(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
            .thenReturn(true);
    when(bidRepository.save(Mockito.any(Bid.class))).thenReturn(bid);
    when(biddingDocumentRepository.save(Mockito.any(BiddingDocument.class))).thenReturn(null);

    // then
    Bid actualResult = bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(bid.getId());
    assertThat(actualResult.getBidPrice()).isEqualTo(bid.getBidPrice());
    logger.info("Response: {}", actualResult.getBidPrice());
  }

  @Test
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
  public void whenGetBidsByForwarder_thenReturnBids() {
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
    Page<Bid> actualPages = bidServiceImpl.getBidsByForwarder(bidder.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getStatus()).isEqualTo(paginationRequest.getStatus());
    assertThat(actualPages.getContent().get(0).getBidder().getId()).isEqualTo(bidder.getId());
  }

  @Test
  @Disabled
  public void whenEditBid_thenReturnBid() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "ACTIVE");
    updates.put("bidPrice", 1000);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setBidValidityPeriod(LocalDateTime.now().minusMinutes(30));

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
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(bidRepository.save(Mockito.any(Bid.class))).thenReturn(null);

    // then
    Bid actualResult = bidServiceImpl.editBid(bid.getId(), bidder.getUsername(), updates);
    assertThat(actualResult).isNotNull();
    logger.info("Response: {}", actualResult.getBidPrice());
  }
}
