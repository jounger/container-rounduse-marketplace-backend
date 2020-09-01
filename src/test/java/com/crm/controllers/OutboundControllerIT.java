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

import com.crm.models.Booking;
import com.crm.models.ContainerType;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.OutboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.OutboundService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class OutboundControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(OutboundControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private OutboundService outboundService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Outbound> pages;

  Outbound outbound;

  List<Outbound> listOutbounds;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  Merchant merchant;

  Booking booking;

  Port port;

  ContainerType containerType;

  ShippingLine shippingLine;

  @BeforeEach
  public void setUp() {

    merchant = new Merchant();
    merchant.setUsername("merchant");

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
    outbound.setCode("123sd2");
    outbound.setMerchant(merchant);
    outbound.setBooking(booking);
    outbound.setContainerType(containerType);
    outbound.setDeliveryTime(timeNow.plusDays(10));
    outbound.setGoodsDescription("Abc");
    outbound.setGrossWeight(1233D);
    outbound.setPackingStation("Ha Noi");
    outbound.setPackingTime(timeNow.plusDays(9));
    outbound.setUnitOfMeasurement("3");
    outbound.setShippingLine(shippingLine);

    listOutbounds = new ArrayList<Outbound>();
    listOutbounds.add(outbound);
    pages = new PageImpl<Outbound>(listOutbounds);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void createOutbound_thenStatusOk_andReturnOutbound() throws Exception {
    // given
    OutboundRequest request = new OutboundRequest();
    request.setCode("123sd2");
    request.setContainerType(containerType.getName());
    request.setShippingLine(shippingLine.getCompanyCode());
    request.setDeliveryTime(LocalDateTime.now().toString());
    when(outboundService.createOutbound(Mockito.anyString(), Mockito.any(OutboundRequest.class))).thenReturn(outbound);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/outbound").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.goodsDescription").value("Abc")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getOutbound_thenStatusOk_andReturnOutbound() throws Exception {
    // given
    when(outboundService.getOutboundById(Mockito.anyLong())).thenReturn(outbound);
    when(outboundService.updateExpiredOutboundFromList(Mockito.anyList())).thenReturn(listOutbounds);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/outbound/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.goodsDescription").value("Abc")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void searchOutbounds_thenStatusOk_andReturnOutbounds() throws Exception {
    // given
    String search = "packingStation:Noi";
    requestParams.add("search", search);
    when(outboundService.searchOutbounds(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);
    when(outboundService.updateExpiredOutboundFromList(Mockito.anyList())).thenReturn(listOutbounds);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/outbound/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].packingStation").value("Ha Noi")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getOutbounds_thenStatusOk_andReturnOutbounds() throws Exception {
    // given
    when(outboundService.getOutbounds(Mockito.any(PaginationRequest.class))).thenReturn(pages);
    when(outboundService.updateExpiredOutboundFromList(Mockito.anyList())).thenReturn(listOutbounds);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/outbound").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].packingStation").value("Ha Noi")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getOutboundsByMerchant_thenStatusOk_andReturnOutbounds() throws Exception {
    // given
    when(outboundService.getOutboundsByMerchant(Mockito.anyString(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);
    when(outboundService.updateExpiredOutboundFromList(Mockito.anyList())).thenReturn(listOutbounds);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/outbound/merchant").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].packingStation").value("Ha Noi")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void editOutbound_thenStatusOk_andReturnOutbound() throws Exception {
    // given
    Map<String, String> updates = new HashMap<String, String>();
    updates.put("packingStation", "Ha Tay");
    outbound.setPackingStation("Ha Tay");
    when(outboundService.editOutbound(Mockito.anyMap(), Mockito.anyLong(), Mockito.anyString())).thenReturn(outbound);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/outbound/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.packingStation").value("Ha Tay")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void deleteOutbound_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/outbound/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa hàng xuất thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
