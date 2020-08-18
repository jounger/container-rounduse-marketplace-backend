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

import com.crm.models.Contract;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.Payment;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PaymentRequest;
import com.crm.services.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class PaymentControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(PaymentControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private PaymentService paymentService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Payment> pages;

  List<Payment> payments;

  LinkedMultiValueMap<String, String> requestParams;

  Payment payment;

  @BeforeEach
  public void setUp() {

    payment = new Payment();
    payment.setId(1L);
    payment.setDetail("123");
    payment.setPaymentDate(LocalDateTime.now());

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setFinesAgainstContractViolations(8D);
    contract.setRequired(false);

    payment.setContract(contract);

    Merchant merchant = new Merchant();
    merchant.setUsername("merchant");
    Forwarder forwarder = new Forwarder();
    forwarder.setUsername("forwarder");
    
    payment.setSender(merchant);
    payment.setRecipient(forwarder);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Payment> payments = new ArrayList<Payment>();
    payments.add(payment);
    pages = new PageImpl<Payment>(payments);
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createPayment_thenStatusOk_andReturnPayment() throws JsonProcessingException, Exception {
    // given
    PaymentRequest request = new PaymentRequest();
    request.setDetail("123");

    when(paymentService.createPayment(Mockito.anyLong(), Mockito.anyString(), Mockito.any(PaymentRequest.class)))
        .thenReturn(payment);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/payment/contract/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.detail").value("123")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getPaymentsByContract_thenStatusOk_andReturnPayments() throws JsonProcessingException, Exception {
    // given
    when(paymentService.getPaymentsByContract(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/payment/contract/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].detail").value("123")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getPaymentsByUser_thenStatusOk_andReturnPayments() throws JsonProcessingException, Exception {
    // given
    when(paymentService.getPaymentsByUser(Mockito.anyString(), Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/payment/user").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].detail").value("123")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void searchPayments_thenStatusOk_andReturnPayments() throws Exception {
    // given
    String search = "detail:123";
    requestParams.add("search", search);
    when(paymentService.searchPayments(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/payment/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].detail").value("123")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editPayment_thenStatusOk_andReturnPayment() throws Exception {
    // given
    payment.setDetail("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("detail", "123456");
    when(paymentService.editPayment(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(payment);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/payment/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.detail").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deletePayment_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/payment/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa hóa đơn thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
