package com.crm.controllers;

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

import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BillOfLading;
import com.crm.models.Booking;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.ContainerTractor;
import com.crm.models.ContainerType;
import com.crm.models.Contract;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Inbound;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.Rating;
import com.crm.models.ShippingLine;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RatingRequest;
import com.crm.services.RatingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class RatingControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(RatingControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private RatingService ratingService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Rating> pages;

  Combined combined;

  Contract contract;

  BiddingDocument biddingDocument;

  Bid bid;

  List<Rating> ratings;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  Merchant merchant;

  Outbound outbound;

  Booking booking;

  Port port;

  ContainerType containerType;

  ShippingLine shippingLine;

  Rating rating;

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

    Forwarder forwarder = new Forwarder();
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

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(2L);

    List<Container> containers = new ArrayList<>();
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
    bid.setValidityPeriod(timeNow.plusHours(1));
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setContainers(containers);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    contract = new Contract();
    contract.setId(1L);
    contract.setFinesAgainstContractViolations(8D);
    contract.setCreationDate(timeNow.minusHours(1));
    contract.setRequired(false);

    combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);
    combined.setIsCanceled(false);
    combined.setContract(contract);

    rating = new Rating();
    rating.setId(1L);
    rating.setContract(contract);
    rating.setComment("123 abc");
    rating.setRatingValue(3);
    rating.setSender(merchant);
    rating.setReceiver(forwarder);

    List<Rating> ratings = new ArrayList<Rating>();
    ratings.add(rating);
    pages = new PageImpl<Rating>(ratings);
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void createRating_thenStatusOk_andReturnContract() throws JsonProcessingException, Exception {
    // given
    RatingRequest request = new RatingRequest();
    request.setRatingValue(3);
    ;
    when(ratingService.createRating(Mockito.anyString(), Mockito.any(RatingRequest.class))).thenReturn(rating);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/rating").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.comment").value("123 abc")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getRatingBySender_thenStatusOk_andReturnContract() throws JsonProcessingException, Exception {
    // given
    when(ratingService.getRatingsBySender(Mockito.anyString(), Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/rating/sender").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].comment").value("123 abc")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getRatingByReceiver_thenStatusOk_andReturnContract() throws JsonProcessingException, Exception {
    // given
    when(ratingService.getRatingsByReceiver(Mockito.anyString(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/rating/receiver").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].comment").value("123 abc")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getRatingsByUser() throws JsonProcessingException, Exception {
    // given
    when(ratingService.getRatingsByUser(Mockito.anyString(), Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/rating/user").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].comment").value("123 abc")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getRatingsByContract_thenStatusOk_andReturnContracts() throws Exception {
    // given
    when(ratingService.getRatingsByContract(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/rating/contract/1").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].comment").value("123 abc")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getRating_thenStatusOk_andReturnContracts() throws JsonProcessingException, Exception {
    // given
    when(ratingService.getRatings(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/rating").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].comment").value("123 abc")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editContract_thenStatusOk_andReturnContract() throws Exception {
    // given
    rating.setComment("1q2w3e");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("comment", "1q2w3e");
    when(ratingService.editRating(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(rating);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/rating/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.comment").value("1q2w3e")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void deleteContract_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/rating/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa đánh giá thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
