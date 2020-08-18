package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;
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

import com.crm.models.Merchant;
import com.crm.models.Permission;
import com.crm.models.Role;
import com.crm.payload.request.MerchantRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.MerchantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class MerchantControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(MerchantControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private MerchantService merchantService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Merchant> pages;

  List<Merchant> merchants;

  LinkedMultiValueMap<String, String> requestParams;

  Merchant merchant;

  @BeforeEach
  public void setUp() {

    merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    Collection<Permission> permissions = new ArrayList<Permission>();
    Permission permission = new Permission();
    permission.setId(1L);
    permission.setName("EDIT");
    role.setPermissions(permissions);

    merchant.setRoles(roles);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Merchant> merchants = new ArrayList<Merchant>();
    merchants.add(merchant);
    pages = new PageImpl<Merchant>(merchants);
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void createMerchant_thenStatusOk_andReturnMerchant() throws JsonProcessingException, Exception {
    // given
    MerchantRequest request = new MerchantRequest();
    request.setUsername("merchant");
    request.setPassword("12342434");
    request.setEmail("mail@gmail.com");
    request.setPhone("0965415415");
    request.setAddress("Ha Tay");
    request.setCompanyAddress("23sad");
    request.setCompanyCode("FOR");
    request.setCompanyDescription("ad2dce");
    request.setCompanyName("Merchant 1wes");
    request.setContactPerson("asd2sdad");
    request.setFax("32321123");
    request.setTin("23d235313");
    request.setWebsite("merchant.com");

    when(merchantService.createMerchant(Mockito.any(MerchantRequest.class))).thenReturn(merchant);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/merchant").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.username").value("merchant")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getMerchant_thenStatusOk_andReturnMerchant() throws JsonProcessingException, Exception {
    // given
    when(merchantService.getMerchant(Mockito.anyLong())).thenReturn(merchant);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/merchant/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("merchant")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getMerchants_thenStatusOk_andReturnMerchants() throws Exception {
    // given
    when(merchantService.getMerchants(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/merchant").contentType(MediaType.APPLICATION_JSON).params(requestParams)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].username").value("merchant")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void editMerchant_thenStatusOk_andReturnMerchant() throws Exception {
    // given
    merchant.setEmail("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("email", "123456");
    when(merchantService.editMerchant(Mockito.anyLong(), Mockito.anyMap())).thenReturn(merchant);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/merchant/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.email").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void deleteMerchant_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/merchant/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa chủ hàng thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
