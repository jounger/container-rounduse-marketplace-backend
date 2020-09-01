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
import com.crm.models.Bid;
import com.crm.models.BillOfLading;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.ContainerTractor;
import com.crm.models.ContainerType;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Inbound;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.ContainerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class ContainerControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(ContainerControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private ContainerService containerService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Container> pages;

  Inbound inbound;

  BillOfLading billOfLading;

  Container container;

  List<Container> containers;

  Bid bid;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  Port port;

  ContainerType containerType;

  ShippingLine shippingLine;

  @BeforeEach
  public void setUp() {

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

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(2L);

    container = new Container();
    container.setId(1L);
    container.setDriver(driver);
    container.setNumber("CN2d2d22");
    container.setTractor(tractor);
    container.setTrailer(trailer);

    billOfLading = new BillOfLading();
    billOfLading.setFreeTime(timeNow.plusDays(10));
    billOfLading.setId(1L);
    billOfLading.setNumber("2esa2ss");
    billOfLading.setPortOfDelivery(port);
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);
    billOfLading.setInbound(inbound);

    inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("C2sd2radasd");
    inbound.setForwarder(forwarder);
    inbound.setBillOfLading(billOfLading);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(timeNow.plusDays(1));
    inbound.setReturnStation("123456");
    inbound.setEmptyTime(timeNow.plusDays(3));
    inbound.setShippingLine(shippingLine);

    bid = new Bid();
    bid.setId(1L);
    bid.setBidder(forwarder);
    bid.setBidDate(timeNow);
    bid.setBidPrice(2300D);
    bid.setFreezeTime(timeNow.plusHours(1));
    bid.setValidityPeriod(timeNow.plusHours(1));
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setContainers(containers);

    containers = new ArrayList<Container>();
    containers.add(container);
    pages = new PageImpl<Container>(containers);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createContainer_thenStatusOk_andReturnContainer() throws JsonProcessingException, Exception {
    // given
    ContainerRequest request = new ContainerRequest();
    request.setNumber("number");
    when(containerService.createContainer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(ContainerRequest.class)))
        .thenReturn(container);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/container/bill-of-lading/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.number").value("CN2d2d22")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getContainer_thenStatusOk_andReturnContainer() throws JsonProcessingException, Exception {
    // given
    when(containerService.getContainerById(Mockito.anyLong())).thenReturn(container);
    when(containerService.updateExpiredContainerFromList(Mockito.anyList())).thenReturn(containers);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/container/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.number").value("CN2d2d22")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getContainersByBillOfLading_thenStatusOk_andReturnContainers() throws JsonProcessingException, Exception {
    // given
    when(containerService.getContainersByBillOfLading(Mockito.anyLong(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);
    when(containerService.updateExpiredContainerFromList(Mockito.anyList())).thenReturn(containers);

    // when and then
    MvcResult result = mockMvc
        .perform(
            get("/api/container/bill-of-lading/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].number").value("CN2d2d22")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getContainersByInbound_thenStatusOk_andReturnContainers() throws JsonProcessingException, Exception {
    // given
    when(containerService.getContainersByInbound(Mockito.anyLong(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);
    when(containerService.updateExpiredContainerFromList(Mockito.anyList())).thenReturn(containers);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/container/inbound/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].number").value("CN2d2d22")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getContainersByBid_thenStatusOk_andReturnContainers() throws JsonProcessingException, Exception {
    // given
    when(containerService.getContainersByBid(Mockito.anyLong(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);
    when(containerService.updateExpiredContainerFromList(Mockito.anyList())).thenReturn(containers);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/container/bid/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].number").value("CN2d2d22")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editContainer_thenStatusOk_andReturnContainer() throws Exception {
    // given
    container.setNumber("2s3d2w");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("number", "2s3d2w");
    when(containerService.editContainer(Mockito.anyMap(), Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(container);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/container/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.number").value("2s3d2w")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deleteContainer_thenStatusOk_andReturnMessage() throws Exception {
    // given

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/container/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa container thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
