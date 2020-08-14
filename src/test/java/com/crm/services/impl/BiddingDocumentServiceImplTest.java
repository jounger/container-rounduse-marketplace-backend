package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.crm.enums.EnumBiddingStatus;
import com.crm.enums.EnumCurrency;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.MerchantRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.UserRepository;

public class BiddingDocumentServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(BiddingDocumentServiceImplTest.class);

  @InjectMocks
  BiddingDocumentServiceImpl biddingDocumentServiceImpl;

  @Mock
  private BiddingDocumentRepository biddingDocumentRepository;

  @Mock
  private MerchantRepository merchantRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private OutboundRepository outboundRepository;

  @Mock
  private ContainerRepository containerRepository;

  @Mock
  private BidRepository bidRepository;

  @Mock
  private CombinedRepository combinedRepository;

  PaginationRequest paginationRequest;

  PaginationRequest paginationRequestHasStatus;

  List<BiddingDocument> biddingDocuments;

  Page<BiddingDocument> pages;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    biddingDocuments = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);

    paginationRequestHasStatus = new PaginationRequest();
    paginationRequestHasStatus.setPage(0);
    paginationRequestHasStatus.setLimit(10);
    paginationRequestHasStatus.setStatus("CREATED");
  }

  @Test
  @DisplayName("Create BiddingDocument success")
  public void whenCreateBiddingDocument_thenReturnBiddingDocument() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocumentRequest request = new BiddingDocumentRequest();
    request.setOutbound(outbound.getId());
    request.setIsMultipleAward(true);
    request.setBidClosing(Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(1)));
    request.setCurrencyOfPayment(EnumCurrency.VND.name());
    request.setBidPackagePrice(1000D);
    request.setBidFloorPrice(100D);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setBidPackagePrice(1000D);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(biddingDocumentRepository.save(Mockito.any(BiddingDocument.class))).thenReturn(biddingDocument);

    // then
    BiddingDocument actualResult = biddingDocumentServiceImpl.createBiddingDocument(offeree.getUsername(), request);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getBidPackagePrice()).isEqualTo(request.getBidPackagePrice());
    logger.info("Response: {}", actualResult.getBidPackagePrice());
  }

  @Test
  @DisplayName("Create BiddingDocument when outbound notfound")
  public void whenCreateBiddingDocument_thenReturnMerchant400() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocumentRequest request = new BiddingDocumentRequest();
    request.setOutbound(outbound.getId());

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.createBiddingDocument(offeree.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create BiddingDocument when outbound notfound")
  public void whenCreateBiddingDocument_thenReturnOutbound400() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocumentRequest request = new BiddingDocumentRequest();
    request.setOutbound(outbound.getId());

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.createBiddingDocument(offeree.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create BiddingDocument when merchant not own outbound")
  public void whenCreateBiddingDocument_thenReturnOutboundIsNotYourException() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Outbound outbound2 = new Outbound();
    outbound2.setId(2L);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound2);

    BiddingDocumentRequest request = new BiddingDocumentRequest();
    request.setOutbound(outbound.getId());

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.createBiddingDocument(offeree.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create BiddingDocument when outbound combined")
  public void whenCreateBiddingDocument_thenReturnOutboundIsCombinedException() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.COMBINED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocumentRequest request = new BiddingDocumentRequest();
    request.setOutbound(outbound.getId());

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      biddingDocumentServiceImpl.createBiddingDocument(offeree.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create BiddingDocument when outbound bidding")
  public void whenCreateBiddingDocument_thenReturnOutboundIsBiddingException() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.BIDDING.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocumentRequest request = new BiddingDocumentRequest();
    request.setOutbound(outbound.getId());

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      biddingDocumentServiceImpl.createBiddingDocument(offeree.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create BiddingDocument when bidClosing time before now")
  public void whenCreateBiddingDocument_thenReturnbidClosingTimeBeforeNowException() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocumentRequest request = new BiddingDocumentRequest();
    request.setOutbound(outbound.getId());
    request.setIsMultipleAward(true);
    request.setBidClosing(Tool.convertLocalDateTimeToString(LocalDateTime.now().minusDays(1)));
    request.setCurrencyOfPayment(EnumCurrency.VND.name());
    request.setBidPackagePrice(1000D);
    request.setBidFloorPrice(100D);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      biddingDocumentServiceImpl.createBiddingDocument(offeree.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create BiddingDocument when bidClosing time after parkingTime")
  public void whenCreateBiddingDocument_thenReturnbidClosingTimeAfterParkingTimeException() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocumentRequest request = new BiddingDocumentRequest();
    request.setOutbound(outbound.getId());
    request.setIsMultipleAward(true);
    request.setBidClosing(Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(3)));
    request.setCurrencyOfPayment(EnumCurrency.VND.name());
    request.setBidPackagePrice(1000D);
    request.setBidFloorPrice(100D);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      biddingDocumentServiceImpl.createBiddingDocument(offeree.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Get BiddingDocument Success")
  public void whenGetBiddingDocument_thenReturnBiddingDocument() {
    // given
    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));

    // then
    BiddingDocument actualResult = biddingDocumentServiceImpl.getBiddingDocument(biddingDocument.getId());
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getStatus()).isEqualTo(EnumBiddingStatus.BIDDING.name());
  }

  @Test
  @DisplayName("Get BiddingDocument NotFound")
  public void whenGetBiddingDocument_thenReturnBiddingDocument404() {
    // given
    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.getBiddingDocument(biddingDocument.getId());
    });
  }

  @Test
  @DisplayName("Get BiddingDocumentByBid Success")
  public void whenGetBiddingDocumentByBid_thenReturnBiddingDocument() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    // when
    when(bidRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(biddingDocumentRepository.findByBid(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(Optional.of(biddingDocument));

    // then
    BiddingDocument actualResult = biddingDocumentServiceImpl.getBiddingDocumentByBid(bid.getId(),
        bidder.getUsername());
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getStatus()).isEqualTo(EnumBiddingStatus.BIDDING.name());
  }

  @Test
  @DisplayName("Get BiddingDocumentByBid when bid notFound")
  public void whenGetBiddingDocumentByBid_thenReturnBid404() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    // when
    when(bidRepository.existsById(Mockito.anyLong())).thenReturn(false);
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.getBiddingDocumentByBid(bid.getId(), bidder.getUsername());
    });
  }

  @Test
  @DisplayName("Get BiddingDocumentByBid when BiddingDocument notFound")
  public void whenGetBiddingDocumentByBid_thenReturnBiddingDocument404() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    // when
    when(bidRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(biddingDocumentRepository.findByBid(Mockito.anyLong(), Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.getBiddingDocumentByBid(bid.getId(), bidder.getUsername());
    });
  }

  @Test
  @DisplayName("Get BiddingDocumentByCombined Success")
  public void whenGetBiddingDocumentByCombined_thenReturnBiddingDocument() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);
    bid.setBiddingDocument(biddingDocument);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    // when
    when(combinedRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(biddingDocumentRepository.findByCombined(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(Optional.of(biddingDocument));

    // then
    BiddingDocument actualResult = biddingDocumentServiceImpl.getBiddingDocumentByCombined(combined.getId(),
        bidder.getUsername());
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Get BiddingDocumentByCombined when Combined notFound")
  public void whenGetBiddingDocumentByCombined_thenReturnCombined404() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);
    bid.setBiddingDocument(biddingDocument);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    // when
    when(combinedRepository.existsById(Mockito.anyLong())).thenReturn(false);
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.getBiddingDocumentByCombined(combined.getId(), bidder.getUsername());
    });
  }

  @Test
  @DisplayName("Get BiddingDocumentByCombined when BiddingDocument notFound")
  public void whenGetBiddingDocumentByCombined_thenReturnBiddingDocument404() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);
    bid.setBiddingDocument(biddingDocument);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    // when
    when(combinedRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(biddingDocumentRepository.findByCombined(Mockito.anyLong(), Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.getBiddingDocumentByCombined(combined.getId(), bidder.getUsername());
    });
  }

  @Test
  @DisplayName("Get BiddingDocumentByExistCombined Success")
  public void whenGetBiddingDocumentByExistCombined_thenReturnBiddingDocument() {
    // given
    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    biddingDocuments.add(biddingDocument);
    pages = new PageImpl<>(biddingDocuments);

    // when
    when(biddingDocumentRepository.findByExistCombined(Mockito.anyString(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Page<BiddingDocument> actualPages = biddingDocumentServiceImpl
        .getBiddingDocumentsByExistCombined(bidder.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getStatus()).isEqualTo(EnumBiddingStatus.BIDDING.name());
  }

  @Test
  @DisplayName("Get BiddingDocuments By Merchant Success")
  public void whenGetBiddingDocumentsByMerchant_thenReturnBiddingDocuments() {
    // given
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    User user = new User();
    user.setId(2L);
    user.setUsername("merchant");
    user.setRoles(roles);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    biddingDocuments.add(biddingDocument);
    pages = new PageImpl<>(biddingDocuments);

    // when
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
    when(biddingDocumentRepository.findByMerchant(Mockito.anyString(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Page<BiddingDocument> actualPages = biddingDocumentServiceImpl.getBiddingDocuments(bidder.getUsername(),
        paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getStatus()).isEqualTo(EnumBiddingStatus.BIDDING.name());
  }

  @Test
  @DisplayName("Get BiddingDocuments By Merchant and status Success")
  public void whenGetBiddingDocumentsByMerchantAndStatus_thenReturnBiddingDocuments() {
    // given
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    User user = new User();
    user.setId(2L);
    user.setUsername("merchant");
    user.setRoles(roles);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    biddingDocuments.add(biddingDocument);
    pages = new PageImpl<>(biddingDocuments);

    // when
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
    when(biddingDocumentRepository.findByMerchant(Mockito.anyString(), Mockito.anyString(),
        Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<BiddingDocument> actualPages = biddingDocumentServiceImpl.getBiddingDocuments(bidder.getUsername(),
        paginationRequestHasStatus);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getStatus()).isEqualTo(EnumBiddingStatus.BIDDING.name());
  }

  @Test
  @DisplayName("Get BiddingDocuments By Forwarder Success")
  public void whenGetBiddingDocumentsByForwarder_thenReturnBiddingDocuments() {
    // given
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    User user = new User();
    user.setId(1L);
    user.setUsername("forwarder");
    user.setRoles(roles);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    biddingDocuments.add(biddingDocument);
    pages = new PageImpl<>(biddingDocuments);

    // when
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
    when(biddingDocumentRepository.findByForwarder(Mockito.anyString(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Page<BiddingDocument> actualPages = biddingDocumentServiceImpl.getBiddingDocuments(bidder.getUsername(),
        paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getStatus()).isEqualTo(EnumBiddingStatus.BIDDING.name());
  }

  @Test
  @DisplayName("Get BiddingDocuments By Forwarder and status Success")
  public void whenGetBiddingDocumentsByForwarderAndStatus_thenReturnBiddingDocuments() {
    // given
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");

    Collection<Role> roles = new ArrayList<>();
    roles.add(role);

    User user = new User();
    user.setId(1L);
    user.setUsername("forwarder");
    user.setRoles(roles);

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(bidder);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    biddingDocuments.add(biddingDocument);
    pages = new PageImpl<>(biddingDocuments);

    // when
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
    when(biddingDocumentRepository.findByForwarder(Mockito.anyString(), Mockito.anyString(),
        Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<BiddingDocument> actualPages = biddingDocumentServiceImpl.getBiddingDocuments(bidder.getUsername(),
        paginationRequestHasStatus);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getStatus()).isEqualTo(EnumBiddingStatus.BIDDING.name());
  }

  @Test
  @DisplayName("Edit BiddingDocument Success")
  public void whenEditBiddingDocument_thenReturnBiddingDocument() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setCurrencyOfPayment(EnumCurrency.VND.name());
    biddingDocument.setBidPackagePrice(1000D);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setStatus(EnumBiddingStatus.CANCELED.name());

    Map<String, Object> updates = new HashMap<>();
    updates.put("bidClosing", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(1)));
    updates.put("currentOfPayment", EnumCurrency.VND.name());
    updates.put("bidPackagePrice", 1000);
    updates.put("bidFloorPrice", 100);
    updates.put("status", EnumBiddingStatus.CANCELED.name());

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(biddingDocumentRepository.save(Mockito.any(BiddingDocument.class))).thenReturn(biddingDocument);

    // then
    BiddingDocument actualResult = biddingDocumentServiceImpl.editBiddingDocument(biddingDocument.getId(), updates);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(biddingDocument.getId());
    assertThat(actualResult.getCurrencyOfPayment()).isEqualTo(biddingDocument.getCurrencyOfPayment());
    assertThat(actualResult.getBidPackagePrice()).isEqualTo(biddingDocument.getBidPackagePrice());
    assertThat(actualResult.getBidFloorPrice()).isEqualTo(biddingDocument.getBidFloorPrice());
    assertThat(actualResult.getStatus()).isEqualTo(biddingDocument.getStatus());
  }

  @Test
  @DisplayName("Edit BiddingDocument when BiddingDocument not found")
  public void whenEditBiddingDocument_thenReturnNotFoundException_BiddingDocument() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setCurrencyOfPayment(EnumCurrency.VND.name());
    biddingDocument.setBidPackagePrice(1000D);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setStatus(EnumBiddingStatus.CANCELED.name());

    Map<String, Object> updates = new HashMap<>();
    updates.put("bidClosing", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(1)));
    updates.put("currentOfPayment", EnumCurrency.VND.name());
    updates.put("bidPackagePrice", 1000);
    updates.put("bidFloorPrice", 100);
    updates.put("status", EnumBiddingStatus.CANCELED.name());

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.editBiddingDocument(biddingDocument.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit BiddingDocument when BiddingDocument Combined")
  public void whenEditBiddingDocument_thenReturnBiddingDocumentCombinedException() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setCurrencyOfPayment(EnumCurrency.VND.name());
    biddingDocument.setBidPackagePrice(1000D);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setStatus(EnumBiddingStatus.COMBINED.name());

    Map<String, Object> updates = new HashMap<>();
    updates.put("bidClosing", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(1)));
    updates.put("currentOfPayment", EnumCurrency.VND.name());
    updates.put("bidPackagePrice", 1000);
    updates.put("bidFloorPrice", 100);
    updates.put("status", EnumBiddingStatus.CANCELED.name());

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      biddingDocumentServiceImpl.editBiddingDocument(biddingDocument.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit BiddingDocument when bidClosingTime Before now")
  public void whenEditBiddingDocument_thenReturnBidClosingTimeBeforeNowException() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setCurrencyOfPayment(EnumCurrency.VND.name());
    biddingDocument.setBidPackagePrice(1000D);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setStatus(EnumBiddingStatus.CANCELED.name());

    Map<String, Object> updates = new HashMap<>();
    updates.put("bidClosing", Tool.convertLocalDateTimeToString(LocalDateTime.now().minusDays(1)));
    updates.put("currentOfPayment", EnumCurrency.VND.name());
    updates.put("bidPackagePrice", 1000);
    updates.put("bidFloorPrice", 100);
    updates.put("status", EnumBiddingStatus.CANCELED.name());

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      biddingDocumentServiceImpl.editBiddingDocument(biddingDocument.getId(), updates);
    });
  }

  @Test
  @DisplayName("Edit BiddingDocument when bidClosingTime after parkingTime")
  public void whenEditBiddingDocument_thenReturnBidClosingTimeAfterParkingTimeException() {
    // given
    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setStatus(EnumSupplyStatus.CREATED.name());
    outbound.setPackingTime(LocalDateTime.now().plusDays(2));

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");
    offeree.getOutbounds().add(outbound);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setIsMultipleAward(true);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    biddingDocument.setCurrencyOfPayment(EnumCurrency.VND.name());
    biddingDocument.setBidPackagePrice(1000D);
    biddingDocument.setBidFloorPrice(100D);
    biddingDocument.setStatus(EnumBiddingStatus.CANCELED.name());

    Map<String, Object> updates = new HashMap<>();
    updates.put("bidClosing", Tool.convertLocalDateTimeToString(LocalDateTime.now().plusDays(3)));
    updates.put("currentOfPayment", EnumCurrency.VND.name());
    updates.put("bidPackagePrice", 1000);
    updates.put("bidFloorPrice", 100);
    updates.put("status", EnumBiddingStatus.CANCELED.name());

    // when
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      biddingDocumentServiceImpl.editBiddingDocument(biddingDocument.getId(), updates);
    });
  }

  @Test
  @DisplayName("Remove BiddingDocument when merchant notFound")
  public void whenRemoveBiddingDocument_thenReturnNotFoundException_Merchant() {
    // given
    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.removeBiddingDocument(biddingDocument.getId(), offeree.getUsername());
    });
  }

  @Test
  @DisplayName("Remove BiddingDocument when BiddingDocument notFound")
  public void whenRemoveBiddingDocument_thenReturnNotFoundException_BiddingDocument() {
    // given
    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.removeBiddingDocument(biddingDocument.getId(), offeree.getUsername());
    });
  }

  @Test
  public void whenRemoveBiddingDocument_thenReturn500_AccessDenied() {
    // given
    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    Merchant Merchant = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant1");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setStatus(EnumBiddingStatus.CANCELED.name());

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(Merchant));
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      biddingDocumentServiceImpl.removeBiddingDocument(biddingDocument.getId(), offeree.getUsername());
    });
  }

  @Test
  public void whenRemoveBiddingDocument_thenReturn500_BiddingDocumentIsInTransaction() {
    // given
    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      biddingDocumentServiceImpl.removeBiddingDocument(biddingDocument.getId(), offeree.getUsername());
    });
  }
}
