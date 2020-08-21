package com.crm.controllers;

import static org.mockito.Mockito.when;
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
import com.crm.payload.request.PaginationRequest;
import com.crm.services.BillOfLadingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class BillOfLadingControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(BillOfLadingControllerIT.class);

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BillOfLadingService billOfLadingService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<BillOfLading> pages;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  Forwarder forwarder;

  BillOfLading billOfLading;

  Port port;

  Inbound inbound;

  ContainerType containerType;

  ShippingLine shippingLine;

  @BeforeEach
  public void setUp() {
    forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    shippingLine = new ShippingLine();
    shippingLine.setCompanyCode("SPL1");

    port = new Port();
    port.setNameCode("PortTest");

    containerType = new ContainerType();
    containerType.setName("CT12");

    Driver driver = new Driver();
    driver.setId(3L);
    driver.setUsername("driver");
    driver.setForwarder(forwarder);

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

    billOfLading = new BillOfLading();
    billOfLading.setFreeTime(timeNow.plusDays(10));
    billOfLading.setId(1L);
    billOfLading.setNumber("2esa2ss");
    billOfLading.setPortOfDelivery(port);
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

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

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void searchBillOfLadings_thenStatusOk_andReturnBillOfLadings() throws Exception {
    // given
    String search = "number:2esa2ss";
    requestParams.add("search", search);
    List<BillOfLading> billOfLadings = new ArrayList<BillOfLading>();
    billOfLadings.add(billOfLading);
    pages = new PageImpl<BillOfLading>(billOfLadings);
    when(billOfLadingService.searchBillOfLadings(Mockito.any(PaginationRequest.class), Mockito.anyString()))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/bill-of-lading/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].number").value("2esa2ss")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBillOfLadingsByInbound_thenStatusOk_andReturnBillOfLadings() throws Exception {
    // given
    when(billOfLadingService.getBillOfLadingByInbound(Mockito.anyLong())).thenReturn(billOfLading);

    // when and then
    MvcResult result = mockMvc
        .perform(
            get("/api/bill-of-lading/inbound/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.number").value("2esa2ss")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBillOfLadingByNumber_thenStatusOk_andReturnBillOfLadings() throws Exception {
    // given
    requestParams = new LinkedMultiValueMap<String, String>();
    requestParams.add("number", "2esa2ss");
    when(billOfLadingService.getBillOfLadingByNumber(Mockito.anyString())).thenReturn(billOfLading);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/bill-of-lading").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.number").value("2esa2ss")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBillOfLading_thenStatusOk_andReturnBillOfLading() throws Exception {
    // given
    when(billOfLadingService.getBillOfLadingById(Mockito.anyLong())).thenReturn(billOfLading);
    // when and then
    MvcResult result = mockMvc.perform(get("/api/bill-of-lading/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.number").value("2esa2ss")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editBillOfLading_thenStatusOk_andReturnBillOfLading() throws Exception {
    // given
    Map<String, String> updates = new HashMap<String, String>();
    updates.put("unit", "5");
    billOfLading.setUnit(5);
    when(billOfLadingService.editBillOfLading(Mockito.anyMap(), Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(billOfLading);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/bill-of-lading/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.unit").value(5)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
