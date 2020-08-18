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

import com.crm.models.Discount;
import com.crm.payload.request.DiscountRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.DiscountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class DiscountControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(DiscountControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private DiscountService discountService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Discount> pages;

  List<Discount> discounts;

  LinkedMultiValueMap<String, String> requestParams;

  Discount discount;

  @BeforeEach
  public void setUp() {

    discount = new Discount();
    discount.setId(1L);
    discount.setCode("Code");
    discount.setExpiredDate(LocalDateTime.now());

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Discount> discounts = new ArrayList<Discount>();
    discounts.add(discount);
    pages = new PageImpl<Discount>(discounts);
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void createDiscount_thenStatusOk_andReturnDiscount() throws JsonProcessingException, Exception {
    // given
    DiscountRequest request = new DiscountRequest();
    request.setCode("Code");
    when(discountService.createDiscount(Mockito.any(DiscountRequest.class))).thenReturn(discount);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/discount").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.code").value("Code")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getDiscount_thenStatusOk_andReturnDiscount() throws JsonProcessingException, Exception {
    // given
    when(discountService.getDiscountById(Mockito.anyLong())).thenReturn(discount);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/discount/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.code").value("Code")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getDiscountByCode_thenStatusOk_andReturnDiscounts() throws Exception {
    // given
    when(discountService.getDiscountByCode(Mockito.anyString())).thenReturn(discount);
    requestParams = new LinkedMultiValueMap<String, String>();
    requestParams.add("code", "Code");
    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/discount").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
            .params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.code").value("Code")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getDiscounts_thenStatusOk_andReturnDiscounts() throws JsonProcessingException, Exception {
    // given
    when(discountService.getDiscounts(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/discount").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].code").value("Code")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void editDiscount_thenStatusOk_andReturnDiscount() throws Exception {
    // given
    discount.setCode("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("code", "123456");
    when(discountService.editDiscount(Mockito.anyMap(), Mockito.anyLong())).thenReturn(discount);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/discount/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.code").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void deleteDiscount_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/discount/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa mã giảm giá thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
