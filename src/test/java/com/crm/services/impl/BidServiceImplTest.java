package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Booking;
import com.crm.models.Container;
import com.crm.models.ContainerType;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
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

  List<Bid> bids = new ArrayList<>();

  Page<Bid> pages;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

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
    booking.setBookingNumber("BL00001");
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
    when(biddingDocumentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(biddingDocument));
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(bidder));
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(containerRepository.existsByOutbound(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
            .thenReturn(true);
    when(bidRepository.save(Mockito.any(Bid.class))).thenReturn(null);
    when(biddingDocumentRepository.save(Mockito.any(BiddingDocument.class))).thenReturn(null);
    // then
    Bid actualResult = bidServiceImpl.createBid(biddingDocument.getId(), bidder.getUsername(), request);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getBidPrice()).isEqualTo(request.getBidPrice());
    logger.info("Response: {}", actualResult.getBidPrice());
  }
}
