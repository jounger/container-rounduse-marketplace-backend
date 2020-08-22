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

import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Booking;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.ContainerTractor;
import com.crm.models.ContainerType;
import com.crm.models.Contract;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.models.ShippingLineNotification;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.ShippingLineNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class ShippingLineNotificationControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(ShippingLineNotificationControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private ShippingLineNotificationService shippingLineNotificationService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<ShippingLineNotification> pages;

  ShippingLineNotification shippingLineNotification;

  ShippingLine shippingLine;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  @BeforeEach
  public void setUp() {
    shippingLineNotification = new ShippingLineNotification();
    shippingLineNotification.setId(1L);
    shippingLineNotification.setIsRead(false);

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

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(2L);
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

    shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setUsername("shippingLine");

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(2L);

    Driver driver = new Driver();
    driver.setId(3L);
    driver.setUsername("driver");

    List<Container> containers = new ArrayList<>();
    Container container = new Container();
    container.setId(1L);
    container.setDriver(driver);
    container.setNumber("CN2d2d22");
    container.setTractor(tractor);
    container.setTrailer(trailer);

    containers.add(container);

    Bid bid = new Bid();
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

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setFinesAgainstContractViolations(8D);
    contract.setCreationDate(timeNow.minusHours(1));
    contract.setRequired(false);

    combined.setContract(contract);

    shippingLineNotification.setRecipient(merchant);
    shippingLineNotification.setRelatedResource(combined);
    shippingLineNotification.setSendDate(timeNow);

    List<ShippingLineNotification> biddingNotifications = new ArrayList<ShippingLineNotification>();
    biddingNotifications.add(shippingLineNotification);
    pages = new PageImpl<ShippingLineNotification>(biddingNotifications);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "shippingline", roles = { "SHIPPINGLINE" })
  void getShippingLineNotifications_thenStatusOk_andReturnShippingLineNotifications() throws Exception {
    // given
    when(shippingLineNotificationService.getShippingLineNotificationsByUsername(Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(
            get("/api/shipping-line-notification").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].isRead").value(false)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "shippingline", roles = { "SHIPPINGLINE" })
  void getShippingLineNotification_thenStatusOk_andReturnShippingLineNotification() throws Exception {
    // given
    when(shippingLineNotificationService.getShippingLineNotification(Mockito.anyLong()))
        .thenReturn(shippingLineNotification);
    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/shipping-line-notification/1").contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.isRead").value(false))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "shippingline", roles = { "SHIPPINGLINE" })
  void editShippingLineNotification_thenStatusOk_andReturnShippingLineNotification() throws Exception {
    // given
    Map<String, String> updates = new HashMap<String, String>();
    updates.put("isRead", "true");
    shippingLineNotification.setIsRead(true);
    when(shippingLineNotificationService.editShippingLineNotification(Mockito.anyLong(), Mockito.anyMap()))
        .thenReturn(shippingLineNotification);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/shipping-line-notification/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.isRead").value(true)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "shippingline", roles = { "SHIPPINGLINE" })
  void deleteShippingLineNotification_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc
        .perform(delete("/api/shipping-line-notification/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa thông báo thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
