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
import com.crm.payload.request.ContainerTractorRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.ContainerTractorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class ContainerTractorControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(ContainerControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private ContainerTractorService tractorService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<ContainerTractor> pages;

  Inbound inbound;

  BillOfLading billOfLading;

  Container container;

  ContainerTractor tractor;

  List<ContainerTractor> tractors;

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

    tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("29A-3231");

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(2L);

    container = new Container();
    container.setId(1L);
    container.setDriver(driver);
    container.setNumber("CN2d2d22");
    container.setTractor(tractor);
    container.setTrailer(trailer);

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

    tractors = new ArrayList<ContainerTractor>();
    tractors.add(tractor);
    pages = new PageImpl<ContainerTractor>(tractors);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createContainerTractor_thenStatusOk_andReturnContainerTractor() throws JsonProcessingException, Exception {
    // given
    ContainerTractorRequest request = new ContainerTractorRequest();
    request.setLicensePlate("29A-3231");
    when(tractorService.createContainerTractor(Mockito.anyString(), Mockito.any(ContainerTractorRequest.class)))
        .thenReturn(tractor);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/container-tractor").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.licensePlate").value("29A-3231")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getContainerTractor_thenStatusOk_andReturnContainerTractor() throws JsonProcessingException, Exception {
    // given
    when(tractorService.getContainerTractorById(Mockito.anyLong())).thenReturn(tractor);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/container-tractor/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.licensePlate").value("29A-3231")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getContainerTractorByLicensePlate_thenStatusOk_andReturnContainerTractor()
      throws JsonProcessingException, Exception {
    // given
    requestParams = new LinkedMultiValueMap<String, String>();
    requestParams.add("licensePlate", "29A-3231");
    when(tractorService.getContainerTractorByLicensePlate(Mockito.anyString())).thenReturn(tractor);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/container-tractor").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.licensePlate").value("29A-3231")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void searchContainerTractors_thenStatusOk_andReturnInbounds() throws Exception {
    // given
    String search = "licensePlate:29A-3231";
    requestParams.add("search", search);
    when(tractorService.searchContainerTractors(Mockito.any(PaginationRequest.class), Mockito.anyString()))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/container-tractor/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].licensePlate").value("29A-3231")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getContainerTractors_thenStatusOk_andReturnContainerTractor() throws JsonProcessingException, Exception {
    // given
    when(tractorService.getContainerTractors(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/container-tractor").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].licensePlate").value("29A-3231")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getContainerTractorsByForwarder_thenStatusOk_andReturnContainerTractors()
      throws JsonProcessingException, Exception {
    // given
    when(tractorService.getContainerTractorsByForwarder(Mockito.anyString(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(
            get("/api/container-tractor/forwarder").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].licensePlate").value("29A-3231")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editContainerTractor_thenStatusOk_andReturnContainerTractor() throws Exception {
    // given
    tractor.setLicensePlate("2s3d2w");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("licensePlate", "2s3d2w");
    when(tractorService.editContainerTractor(Mockito.anyMap(), Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(tractor);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/container-tractor/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.licensePlate").value("2s3d2w")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deleteContainerTractor_thenStatusOk_andReturnMessage() throws Exception {
    // given

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/container-tractor/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("ContainerTractor has remove successfully")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
