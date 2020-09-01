package com.crm.controllers;

import static org.mockito.Mockito.doNothing;
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
import com.crm.models.Invoice;
import com.crm.models.Merchant;
import com.crm.payload.request.InvoiceRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.InvoiceService;
import com.crm.websocket.controller.NotificationBroadcast;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class InvoiceControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(InvoiceControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private InvoiceService invoiceService;

  @MockBean
  private NotificationBroadcast notificationBroadcast;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Invoice> pages;

  List<Invoice> invoices;

  LinkedMultiValueMap<String, String> requestParams;

  Invoice invoice;

  @BeforeEach
  public void setUp() {

    invoice = new Invoice();
    invoice.setId(1L);
    invoice.setDetail("123");
    invoice.setPaymentDate(LocalDateTime.now());
    invoice.setIsPaid(true);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setFinesAgainstContractViolations(8D);
    contract.setRequired(false);
    contract.setCreationDate(LocalDateTime.now());

    invoice.setContract(contract);

    Merchant merchant = new Merchant();
    merchant.setUsername("merchant");
    Forwarder forwarder = new Forwarder();
    forwarder.setUsername("forwarder");

    invoice.setSender(merchant);
    invoice.setRecipient(forwarder);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Invoice> payments = new ArrayList<Invoice>();
    payments.add(invoice);
    pages = new PageImpl<Invoice>(payments);
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createInvoice_thenStatusOk_andReturnInvoice() throws JsonProcessingException, Exception {
    // given
    InvoiceRequest request = new InvoiceRequest();
    request.setDetail("123");

    doNothing().when(notificationBroadcast).broadcastCreateInvoiceToUser(Mockito.any(Invoice.class));
    when(invoiceService.createInvoice(Mockito.anyLong(), Mockito.anyString(), Mockito.any(InvoiceRequest.class)))
        .thenReturn(invoice);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/invoice/contract/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.detail").value("123")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getInvoicesByContract_thenStatusOk_andReturnInvoices() throws JsonProcessingException, Exception {
    // given
    when(invoiceService.getInvoicesByContract(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/invoice/contract/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].detail").value("123")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getInvoices_thenStatusOk_andReturnInvoices() throws JsonProcessingException, Exception {
    // given
    when(invoiceService.getInvoicesByUser(Mockito.anyString(), Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/invoice").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].detail").value("123")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void searchInvoices_thenStatusOk_andReturnInvoices() throws Exception {
    // given
    String search = "detail:123";
    requestParams.add("search", search);
    when(invoiceService.searchInvoices(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/invoice/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].detail").value("123")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editInvoice_thenStatusOk_andReturnInvoice() throws Exception {
    // given
    invoice.setDetail("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("detail", "123456");
    when(invoiceService.editInvoice(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(invoice);
    doNothing().when(notificationBroadcast).broadcastSendAcceptInvoiceToUser(Mockito.any(Invoice.class));
    doNothing().when(notificationBroadcast).broadcastSendRejectInvoiceToUser(Mockito.any(Invoice.class));

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/invoice/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.detail").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deleteInvoice_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/invoice/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa hóa đơn thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
