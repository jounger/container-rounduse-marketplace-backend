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
import com.crm.payload.request.InboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.InboundService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class InboundControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(InboundControllerIT.class);

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private InboundService inboundService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Inbound> pages;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  Forwarder forwarder;

  BillOfLading billOfLading;

  Inbound inbound;

  Port port;

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
    container.setContainerNumber("CN2d2d22");
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

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createInbound_thenStatusOk_andReturnInbound() throws Exception {
    // given
    InboundRequest request = new InboundRequest();
    request.setReturnStation("123456");
    request.setCode("S1sds");
    request.setShippingLine(shippingLine.getCompanyCode());
    request.setContainerType("123sd123");
    request.setPickupTime(inbound.getPickupTime().toString());
    when(inboundService.createInbound(Mockito.anyString(), Mockito.any(InboundRequest.class))).thenReturn(inbound);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/inbound").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.returnStation").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void searchInbounds_thenStatusOk_andReturnInbounds() throws Exception {
    // given
    String search = "number:BK123w22";
    requestParams.add("search", search);
    List<Inbound> inbounds = new ArrayList<Inbound>();
    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);
    when(inboundService.searchInbounds(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/inbound/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].code").value("C2sd2radasd")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getInbounds_thenStatusOk_andReturnInbounds() throws Exception {
    // given
    List<Inbound> inbounds = new ArrayList<Inbound>();
    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);
    when(inboundService.getInbounds(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/inbound").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].code").value("C2sd2radasd")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getInboundsByForwarder_thenStatusOk_andReturnInbounds() throws Exception {
    // given
    List<Inbound> inbounds = new ArrayList<Inbound>();
    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);
    when(inboundService.getInboundsByForwarder(Mockito.anyString(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/inbound/forwarder").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].code").value("C2sd2radasd")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }
  
  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getInboundsByOutbound_thenStatusOk_andReturnInbounds() throws Exception {
    // given
    List<Inbound> inbounds = new ArrayList<Inbound>();
    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);
    when(inboundService.getInboundsByOutbound(Mockito.anyLong(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/inbound/outbound/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].code").value("C2sd2radasd")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }
  
  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getInboundByContainer_thenStatusOk_andReturnInbound() throws Exception {
    // given
    inbound.setReturnStation("654321");
    when(inboundService.getInboundByContainer(Mockito.anyLong())).thenReturn(inbound);
    // when and then
    MvcResult result = mockMvc.perform(get("/api/inbound/container/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.returnStation").value("654321")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }
  
  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getInboundsByOutboundAndForwarder_thenStatusOk_andReturnInbounds() throws Exception {
    // given
    List<Inbound> inbounds = new ArrayList<Inbound>();
    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);
    when(inboundService.getInboundsByOutboundAndForwarder(Mockito.anyLong(), Mockito.anyString(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/inbound/outbound-match/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].code").value("C2sd2radasd")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getInbound_thenStatusOk_andReturnInbound() throws Exception {
    // given
    inbound.setReturnStation("654321");
    when(inboundService.getInboundById(Mockito.anyLong())).thenReturn(inbound);
    // when and then
    MvcResult result = mockMvc.perform(get("/api/inbound/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.returnStation").value("654321")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editInbound_thenStatusOk_andReturnInbound() throws Exception {
    // given
    Map<String, String> updates = new HashMap<String, String>();
    updates.put("returnStation", "654321");
    inbound.setReturnStation("654321");
    when(inboundService.editInbound(Mockito.anyMap(), Mockito.anyLong(), Mockito.anyString())).thenReturn(inbound);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/inbound/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.returnStation").value("654321")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deleteInbound_thenStatusOk_andReturnMessage() throws Exception {
    // given

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/inbound/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Inbound has remove successfully")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
