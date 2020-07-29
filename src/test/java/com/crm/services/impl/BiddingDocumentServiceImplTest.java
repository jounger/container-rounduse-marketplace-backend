package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.DiscountRepository;
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
  private DiscountRepository discountRepository;

  @Mock
  private ContainerRepository containerRepository;

  @Mock
  private BidRepository bidRepository;

  PaginationRequest paginationRequest;

  List<BiddingDocument> biddingDocuments;

  Page<BiddingDocument> pages;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    biddingDocuments = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
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
    request.setBidDiscountCode(null);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setBidPackagePrice(1000D);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(discountRepository.findByCode(Mockito.anyString())).thenReturn(Optional.empty());
    when(biddingDocumentRepository.save(Mockito.any(BiddingDocument.class))).thenReturn(biddingDocument);

    // then
    BiddingDocument actualResult = biddingDocumentServiceImpl.createBiddingDocument(offeree.getUsername(), request);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getBidPackagePrice()).isEqualTo(request.getBidPackagePrice());
    logger.info("Response: {}", actualResult.getBidPackagePrice());
  }

  @Test
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
  public void whenRemoveBiddingDocument_thenReturn404() {
    // given
    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);

    // when
    when(merchantRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      biddingDocumentServiceImpl.removeBiddingDocument(biddingDocument.getId(), offeree.getUsername());
    });
  }

  @Test
  public void whenRemoveBiddingDocument_thenReturn500() {
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
