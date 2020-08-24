package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

import com.crm.enums.EnumBiddingStatus;
import com.crm.enums.EnumReportStatus;
import com.crm.models.BiddingDocument;
import com.crm.models.Booking;
import com.crm.models.ContainerType;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.Report;
import com.crm.models.ReportNotification;
import com.crm.models.ShippingLine;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.ReportNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class ReportNotificationControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(ReportNotificationControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private ReportNotificationService reportNotificationService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<ReportNotification> pages;

  ReportNotification reportNotification;

  Report report;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  @BeforeEach
  public void setUp() {
    reportNotification = new ReportNotification();
    reportNotification.setId(1L);
    reportNotification.setIsRead(false);
    
    Merchant merchant = new Merchant();
    merchant.setId(2L);
    merchant.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setBidOpening(timeNow);
    biddingDocument.setBidClosing(timeNow.plusHours(8));
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(2000D);
    biddingDocument.setPriceLeadership(2000D);
    biddingDocument.setBidPackagePrice(3000D);
    biddingDocument.setCurrencyOfPayment("VND");
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    biddingDocument.setOfferee(merchant);
    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setCompanyCode("SPL1");

    Port port = new Port();
    port.setNameCode("PortTest");

    ContainerType containerType = new ContainerType();
    containerType.setName("CT12");

    Booking booking = new Booking();
    booking.setNumber("BK123w22");
    booking.setUnit(3);
    booking.setCutOffTime(timeNow.plusDays(30));
    booking.setIsFcl(false);
    booking.setPortOfLoading(port);

    Outbound outbound = new Outbound();
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

    report = new Report();
    report.setId(1L);
    report.setSender(forwarder);
    report.setTitle("title");
    report.setDetail("detail");
    report.setReport(biddingDocument);
    report.setSendDate(timeNow);
    report.setStatus(EnumReportStatus.PENDING.name());

    reportNotification.setRecipient(shippingLine);
    reportNotification.setRelatedResource(report);
    reportNotification.setSendDate(timeNow);

    List<ReportNotification> biddingNotifications = new ArrayList<ReportNotification>();
    biddingNotifications.add(reportNotification);
    pages = new PageImpl<ReportNotification>(biddingNotifications);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getReportNotifications_thenStatusOk_andReturnReportNotifications() throws Exception {
    // given
    when(reportNotificationService.getReportNotificationsByUsername(Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/report-notification").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].isRead").value(false)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getReportNotification_thenStatusOk_andReturnReportNotification() throws Exception {
    // given
    when(reportNotificationService.getReportNotification(Mockito.anyLong())).thenReturn(reportNotification);
    // when and then
    MvcResult result = mockMvc.perform(get("/api/report-notification/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.isRead").value(false)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editReportNotification_thenStatusOk_andReturnReportNotification() throws Exception {
    // given
    Map<String, String> updates = new HashMap<String, String>();
    updates.put("isRead", "true");
    reportNotification.setIsRead(true);
    when(reportNotificationService.editReportNotification(Mockito.anyLong(), Mockito.anyMap()))
        .thenReturn(reportNotification);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/report-notification/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.isRead").value(true)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deleteReportNotification_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc
        .perform(delete("/api/report-notification/1").contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa thông báo thành công")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
