package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.crm.payload.request.OverviewRequest;
import com.crm.payload.response.ForwarderOverviewResponse;
import com.crm.payload.response.MerchantOverviewResponse;
import com.crm.payload.response.OperatorOverviewResponse;
import com.crm.payload.response.ShippingLineOverviewResponse;
import com.crm.services.OverviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class OverviewControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(OverviewControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private OverviewService overviewService;

  @Autowired
  private ObjectMapper objectMapper;

  LocalDateTime timeNow = LocalDateTime.now();

  OverviewRequest request;

  OperatorOverviewResponse operatorResponse;

  MerchantOverviewResponse merchantResponse;

  ForwarderOverviewResponse forwardResponse;

  ShippingLineOverviewResponse shippingLineResponse;

  @BeforeEach
  public void setUp() {
    request = new OverviewRequest();
    request.setStartDate("2020-08-10T20:22");
    request.setEndDate("2020-09-10T20:22");
    operatorResponse = new OperatorOverviewResponse(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    merchantResponse = new MerchantOverviewResponse(1, 1, 1, 1, 1, 1, 1);
    forwardResponse = new ForwarderOverviewResponse(1, 1, 1, 1, 1, 1, 1, 1);
    shippingLineResponse = new ShippingLineOverviewResponse(1, 1);
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void overviewByOperator_thenStatusOk_andReturnOperatorOverviewResponse() throws JsonProcessingException, Exception {
    // given
    when(overviewService.getOverviewByOperator(Mockito.any(OverviewRequest.class))).thenReturn(operatorResponse);
    when(overviewService.getOverviewByForwarder(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(forwardResponse);
    when(overviewService.getOverviewByMerchant(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(merchantResponse);
    when(overviewService.getOverviewByShippingLine(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(shippingLineResponse);
    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/overview").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.outboundQty").value(1))
        .andExpect(jsonPath("$.inboundQty").value(1)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());

  }
  
  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void overviewByMerchant_thenStatusOk_andReturnMerchantOverviewResponse() throws JsonProcessingException, Exception {
    // given
    when(overviewService.getOverviewByOperator(Mockito.any(OverviewRequest.class))).thenReturn(operatorResponse);
    when(overviewService.getOverviewByForwarder(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(forwardResponse);
    when(overviewService.getOverviewByMerchant(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(merchantResponse);
    when(overviewService.getOverviewByShippingLine(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(shippingLineResponse);
    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/overview").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.outboundQty").value(1))
        .andExpect(jsonPath("$.biddedOutboundQty").value(1)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());

  }
  
  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void overviewByForwarder_thenStatusOk_andReturnForwarderOverviewResponse() throws JsonProcessingException, Exception {
    // given
    when(overviewService.getOverviewByOperator(Mockito.any(OverviewRequest.class))).thenReturn(operatorResponse);
    when(overviewService.getOverviewByForwarder(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(forwardResponse);
    when(overviewService.getOverviewByMerchant(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(merchantResponse);
    when(overviewService.getOverviewByShippingLine(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(shippingLineResponse);
    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/overview").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.containerQty").value(1))
        .andExpect(jsonPath("$.inboundQty").value(1)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());

  }
  
  @Test
  @WithMockUser(username = "shippingline", roles = { "SHIPPINGLINE" })
  void overviewByShippingLine_thenStatusOk_andReturnShippingLineOverviewResponse() throws JsonProcessingException, Exception {
    // given
    when(overviewService.getOverviewByOperator(Mockito.any(OverviewRequest.class))).thenReturn(operatorResponse);
    when(overviewService.getOverviewByForwarder(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(forwardResponse);
    when(overviewService.getOverviewByMerchant(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(merchantResponse);
    when(overviewService.getOverviewByShippingLine(Mockito.anyString(), Mockito.any(OverviewRequest.class)))
        .thenReturn(shippingLineResponse);
    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/overview").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.biddingContainerQty").value(1))
        .andExpect(jsonPath("$.combinedContainerQty").value(1)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());

  }

}
