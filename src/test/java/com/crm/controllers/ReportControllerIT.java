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

import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
import com.crm.enums.EnumReportStatus;
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
import com.crm.models.Report;
import com.crm.models.ShippingLine;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReportRequest;
import com.crm.services.ReportService;
import com.crm.websocket.controller.NotificationBroadcast;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class ReportControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(ReportControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private ReportService reportService;

  @Autowired
  private NotificationBroadcast notificationBroadcast;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Report> pages;

  Combined combined;

  Contract contract;

  BiddingDocument biddingDocument;

  Bid bid;

  List<Report> reports;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  Merchant merchant;

  Outbound outbound;

  Booking booking;

  Port port;

  ContainerType containerType;

  ShippingLine shippingLine;

  Report report;

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

    report = new Report();
    report.setId(1L);
    report.setSender(merchant);
    report.setTitle("title");
    report.setDetail("detail");
    report.setReport(biddingDocument);
    report.setSendDate(timeNow);
    report.setStatus(EnumReportStatus.PENDING.name());

    List<Report> reports = new ArrayList<Report>();
    reports.add(report);
    pages = new PageImpl<Report>(reports);
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createReport_thenStatusOk_andReturnContract() throws JsonProcessingException, Exception {
    // given
    ReportRequest request = new ReportRequest();
    request.setTitle("title");
    doNothing().when(notificationBroadcast).broadcastCreateReportToModerator(Mockito.any(Report.class));
    when(reportService.createReport(Mockito.anyString(), Mockito.any(ReportRequest.class))).thenReturn(report);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/report").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.title").value("title")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getReport_thenStatusOk_andReturnContract() throws JsonProcessingException, Exception {
    // given
    when(reportService.getReport(Mockito.anyLong(), Mockito.anyString())).thenReturn(report);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/report/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("title")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void searchReports_thenStatusOk_andReturnContract() throws JsonProcessingException, Exception {
    // given
    String search = "title:title";
    requestParams.add("search", search);
    when(reportService.searchReports(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/report/filter").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].title").value("title")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getReport_thenStatusOk_andReturnContracts() throws JsonProcessingException, Exception {
    // given
    when(reportService.getReports(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/report").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].title").value("title")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editReport_thenStatusOk_andReturnContract() throws Exception {
    // given
    report.setTitle("1q2w3e");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("title", "1q2w3e");
    when(reportService.getReport(Mockito.anyLong(), Mockito.anyString())).thenReturn(report);
    when(reportService.editReport(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(report);
    doNothing().when(notificationBroadcast).broadcastCreateReportToModerator(Mockito.any(Report.class));

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/report/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.title").value("1q2w3e")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deleteReport_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/report/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa báo cáo thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
