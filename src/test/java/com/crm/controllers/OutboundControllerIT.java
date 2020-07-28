package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

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
  private MockMvc mockMvc;

  @MockBean
  private OutboundService outboundService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Outbound> pages;

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
    merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    shippingLine = new ShippingLine();
    shippingLine.setCompanyCode("SPL1");

    port = new Port();
    port.setNameCode("PortTest");

    containerType = new ContainerType();
    containerType.setName("CT12");

    booking = new Booking();
    booking.setBookingNumber("BK123w22");
    booking.setUnit(3);
    booking.setCutOffTime(timeNow.plusDays(30));
    booking.setIsFcl(false);
    booking.setPortOfLoading(port);

    outbound = new Outbound();
    outbound.setId(1L);
    outbound.setMerchant(merchant);
    outbound.setBooking(booking);
    outbound.setContainerType(containerType);
    outbound.setDeliveryTime(timeNow.plusDays(10));
    outbound.setGoodsDescription("Abc");
    outbound.setGrossWeight(1233D);
    outbound.setPackingStation("123456");
    outbound.setPackingTime(timeNow.plusDays(9));
    outbound.setUnitOfMeasurement("3");
    outbound.setShippingLine(shippingLine);
    
    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void createOutbound_thenStatusOk_andReturnOutbound() throws Exception {
    // given
    OutboundRequest request = new OutboundRequest();
    request.setPackingStation("123456");
    when(outboundService.createOutbound(Mockito.anyString(), Mockito.any(OutboundRequest.class))).thenReturn(outbound);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/outbound").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.packingStation").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
