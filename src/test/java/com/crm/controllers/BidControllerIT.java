package com.crm.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BillOfLading;
import com.crm.models.Booking;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.ContainerTractor;
import com.crm.models.ContainerType;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Inbound;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReplaceContainerRequest;
import com.crm.services.BidService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class BidControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(BidControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private BidService bidService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Bid> pages;

  BiddingDocument biddingDocument;

  Bid bid;

  List<Bid> listBids;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  Merchant merchant;

  Outbound outbound;

  Booking booking;

  Port port;

  ContainerType containerType;

  ShippingLine shippingLine;

  Forwarder forwarder;

  ContainerTractor tractor;

  ContainerSemiTrailer trailer;

  List<Container> containers;

  @BeforeEach
  public void setUp() {
    biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setBidOpening(timeNow);
    biddingDocument.setBidClosing(timeNow.plusHours(8));
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(2000D);
    biddingDocument.setPriceLeadership(2000D);
    biddingDocument.setBidPackagePrice(3000D);
    biddingDocument.setCurrencyOfPayment("VND");
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    biddingDocument.setOfferee(merchant);

    forwarder = new Forwarder();
    forwarder.setId(2L);
    forwarder.setUsername("forwarder");

    Driver driver = new Driver();
    driver.setId(3L);
    driver.setUsername("driver");
    driver.setForwarder(forwarder);

    shippingLine = new ShippingLine();
    shippingLine.setCompanyCode("SPL1");

    port = new Port();
    port.setNameCode("PortTest");

    containerType = new ContainerType();
    containerType.setName("CT12");

    booking = new Booking();
    booking.setNumber("BK123w22");
    booking.setUnit(3);
    booking.setCutOffTime(timeNow.plusDays(30));
    booking.setIsFcl(false);
    booking.setPortOfLoading(port);

    outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setContainerType(containerType);
    outbound.setDeliveryTime(timeNow.plusDays(10));
    outbound.setGoodsDescription("Abc");
    outbound.setGrossWeight(1233D);
    outbound.setPackingStation("Ha Noi");
    outbound.setPackingTime(timeNow.plusDays(9));
    outbound.setUnitOfMeasurement("3");
    outbound.setShippingLine(shippingLine);

    biddingDocument.setOutbound(outbound);

    tractor = new ContainerTractor();
    tractor.setId(1L);

    trailer = new ContainerSemiTrailer();
    trailer.setId(2L);

    containers = new ArrayList<>();
    Container container = new Container();
    container.setId(1L);
    container.setDriver(driver);
    container.setNumber("CN2d2d22");
    container.setTractor(tractor);
    container.setTrailer(trailer);

    containers.add(container);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setForwarder(forwarder);
    inbound.setEmptyTime(timeNow.plusDays(10));
    inbound.setPickupTime(timeNow.plusDays(9));

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setFreeTime(timeNow.plusDays(12));
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.getContainers().add(container);
    billOfLading.setInbound(inbound);

    bid = new Bid();
    bid.setId(1L);
    bid.setBidder(forwarder);
    bid.setBiddingDocument(biddingDocument);
    bid.setBidDate(timeNow);
    bid.setBidPrice(2300D);
    bid.setFreezeTime(timeNow.plusHours(1));
    bid.setValidityPeriod(timeNow.plusHours(3));
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setContainers(containers);

    listBids = new ArrayList<Bid>();
    listBids.add(bid);
    pages = new PageImpl<Bid>(listBids);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createBid_thenStatusCreated_andReturnBid() throws JsonProcessingException, Exception {
    // given
    BidRequest request = new BidRequest();
    request.setBidDate(timeNow.toString());
    when(bidService.createBid(Mockito.anyLong(), Mockito.anyString(), Mockito.any(BidRequest.class))).thenReturn(bid);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/bid/bidding-document/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.bidDate").value(Tool.convertLocalDateTimeToString(timeNow))).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getBid_thenStatusOk_andReturnBid() throws JsonProcessingException, Exception {
    // given
    when(bidService.getBid(Mockito.anyLong(), Mockito.anyString())).thenReturn(bid);
    when(bidService.updateExpiredBidFromList(Mockito.anyList())).thenReturn(listBids);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/bid/1").contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.bidDate").value(Tool.convertLocalDateTimeToString(timeNow))).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getBidsByBiddingDocumentAndExistCombined_thenStatusOk_andReturnBid() throws JsonProcessingException, Exception {
    // given
    when(bidService.getBidsByBiddingDocumentAndExistCombined(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages);
    when(bidService.updateExpiredBidFromList(Mockito.anyList())).thenReturn(listBids);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/bid/combined/bidding-document/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].bidDate").value(Tool.convertLocalDateTimeToString(timeNow))).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBidsByBiddingDocument_thenStatusOk_andReturnBids() throws JsonProcessingException, Exception {
    // given
    when(bidService.getBidsByBiddingDocument(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages);
    when(bidService.updateExpiredBidFromList(Mockito.anyList())).thenReturn(listBids);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/bid/bidding-document/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].bidDate").value(Tool.convertLocalDateTimeToString(timeNow))).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editBid_thenStatusOk_andReturnBid() throws Exception {
    // given
    bid.setBidPrice(2800D);
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("bidPrice", "2800");
    when(bidService.getBid(Mockito.anyLong(), Mockito.anyString())).thenReturn(bid);
    when(bidService.editBid(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(bid);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/bid/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.bidPrice").value(2800D)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void addContainers_thenStatusOk_andReturnBid() throws Exception {
    // given
    Driver driver2 = new Driver();
    driver2.setId(4L);
    driver2.setUsername("driver");
    driver2.setForwarder(forwarder);
    tractor = new ContainerTractor();
    tractor.setId(3L);
    tractor.setLicensePlate("123sa");
    trailer = new ContainerSemiTrailer();
    trailer.setId(4L);
    trailer.setLicensePlate("123ss");
    Container container2 = new Container();
    container2.setId(5L);
    container2.setDriver(driver2);
    container2.setTractor(tractor);
    container2.setTrailer(trailer);
    container2.setNumber("312c3");
    BidRequest request = new BidRequest();
    request.getContainers().add(5L);
    when(bidService.addContainer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(BidRequest.class)))
        .thenReturn(bid);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/bid/1/container").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.bidPrice").value(2300D)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void removeContainer_thenStatusOk_andReturnBid() throws Exception {
    // given
    Driver driver2 = new Driver();
    driver2.setId(4L);
    driver2.setUsername("driver");
    driver2.setForwarder(forwarder);
    tractor = new ContainerTractor();
    tractor.setId(3L);
    tractor.setLicensePlate("123sa");
    trailer = new ContainerSemiTrailer();
    trailer.setId(4L);
    trailer.setLicensePlate("123ss");
    Container container2 = new Container();
    container2.setId(5L);
    container2.setDriver(driver2);
    container2.setTractor(tractor);
    container2.setTrailer(trailer);
    container2.setNumber("312c3");
    BidRequest request = new BidRequest();
    request.getContainers().add(5L);
    when(bidService.removeContainer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong())).thenReturn(bid);

    // when and then
    MvcResult result = mockMvc
        .perform(delete("/api/bid/1/container/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.bidPrice").value(2300D)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void replaceContainer_thenStatusOk_andReturnBid() throws Exception {
    // given
    Driver driver2 = new Driver();
    driver2.setId(4L);
    driver2.setUsername("driver");
    driver2.setForwarder(forwarder);
    tractor = new ContainerTractor();
    tractor.setId(3L);
    tractor.setLicensePlate("123sa");
    trailer = new ContainerSemiTrailer();
    trailer.setId(4L);
    trailer.setLicensePlate("123ss");
    Container container2 = new Container();
    container2.setId(5L);
    container2.setDriver(driver2);
    container2.setTractor(tractor);
    container2.setTrailer(trailer);
    container2.setNumber("312c3");
    bid.getContainers().add(container2);
    ReplaceContainerRequest request = new ReplaceContainerRequest();
    request.setOldContainerId(1L);
    request.setOldContainerId(5L);
    when(
        bidService.replaceContainer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(ReplaceContainerRequest.class)))
            .thenReturn(bid);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/bid/1/container").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.bidPrice").value(2300D)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deleteBid_thenStatusOk_andReturnMessage() throws Exception {
    // given
    // when(bidService.removeBid(Mockito.anyLong(),
    // Mockito.anyString())).thenReturn(bid);
    when(bidService.getBid(Mockito.anyLong(), Mockito.anyString())).thenReturn(bid);
    doNothing().when(bidService).removeBid(Mockito.anyLong(), Mockito.anyString());

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/bid/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Xóa hồ sơ dự thầu thành công")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
