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

import com.crm.common.Tool;
import com.crm.enums.EnumBiddingStatus;
import com.crm.models.BiddingDocument;
import com.crm.models.Booking;
import com.crm.models.ContainerType;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.BiddingDocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class BiddingDocumentControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(BiddingDocumentControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private BiddingDocumentService biddingDocumentService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<BiddingDocument> pages;

  BiddingDocument biddingDocument;

  List<BiddingDocument> listBidDoc;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  Merchant merchant;

  Outbound outbound;

  Booking booking;

  Port port;

  ContainerType containerType;

  ShippingLine shippingLine;

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
    merchant.setUsername("merchant");
    biddingDocument.setOfferee(merchant);

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

    listBidDoc = new ArrayList<BiddingDocument>();
    listBidDoc.add(biddingDocument);
    pages = new PageImpl<BiddingDocument>(listBidDoc);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void createBiddingDocument_thenStatusOk_andReturnBiddingDocument() throws Exception {
    // given
    BiddingDocumentRequest request = new BiddingDocumentRequest();
    request.setBidOpening(LocalDateTime.now().toString());
    when(biddingDocumentService.createBiddingDocument(Mockito.anyString(), Mockito.any(BiddingDocumentRequest.class)))
        .thenReturn(biddingDocument);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/bidding-document").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.bidOpening").value(Tool.convertLocalDateTimeToString(timeNow))).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBiddingDocument_thenStatusOk_andReturnBiddingDocument() throws Exception {
    // given
    when(biddingDocumentService.getBiddingDocument(Mockito.anyLong())).thenReturn(biddingDocument);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/bidding-document/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.bidOpening").value(Tool.convertLocalDateTimeToString(timeNow))).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBiddingDocuments_thenStatusOk_andReturnBiddingDocuments() throws Exception {
    // given
    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
    when(biddingDocumentService.getBiddingDocuments(Mockito.anyString(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/bidding-document").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].offeree").value("merchant")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBiddingDocumentByBid_thenStatusOk_andReturnBiddingDocument() throws Exception {
    // given
    when(biddingDocumentService.getBiddingDocumentByBid(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(biddingDocument);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/bidding-document/bid/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.offeree").value("merchant")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBiddingDocumentsByExistCombined_thenStatusOk_andReturnNull() throws Exception {
    // given
    // pages = Mockito.mock(Page.class);
    List<BiddingDocument> listBidDoc1 = new ArrayList<BiddingDocument>();
    Page<BiddingDocument> pages1 = new PageImpl<BiddingDocument>(listBidDoc1);
    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
    when(biddingDocumentService.getBiddingDocumentsByExistCombined(Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages1);

    // when and then
    MvcResult result = mockMvc
        .perform(
            get("/api/bidding-document/combined").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void updateBiddingDocument_thenStatusOk_andReturnBiddingDocument() throws Exception {
    // given
    biddingDocument.setBidFloorPrice(1800D);
    when(biddingDocumentService.editBiddingDocument(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(biddingDocument);
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("bidFloorPrice", "1800");

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/bidding-document/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.bidFloorPrice").value(1800D)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void deleteBiddingDocument_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/bidding-document/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Xóa hồ sơ đấu thầu thành công")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
