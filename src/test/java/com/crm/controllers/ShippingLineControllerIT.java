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

import com.crm.models.ShippingLine;
import com.crm.models.Permission;
import com.crm.models.Role;
import com.crm.payload.request.ShippingLineRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.ShippingLineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class ShippingLineControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(ShippingLineControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private ShippingLineService shippingLineService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<ShippingLine> pages;

  List<ShippingLine> shippingLines;

  LinkedMultiValueMap<String, String> requestParams;

  ShippingLine shippingLine;

  @BeforeEach
  public void setUp() {

    shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setUsername("shippingLine");
    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_SHIPPINGLINE");
    Collection<Permission> permissions = new ArrayList<Permission>();
    Permission permission = new Permission();
    permission.setId(1L);
    permission.setName("EDIT");
    role.setPermissions(permissions);

    shippingLine.setRoles(roles);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<ShippingLine> shippingLines = new ArrayList<ShippingLine>();
    shippingLines.add(shippingLine);
    pages = new PageImpl<ShippingLine>(shippingLines);
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void createShippingLine_thenStatusOk_andReturnShippingLine() throws JsonProcessingException, Exception {
    // given
    ShippingLineRequest request = new ShippingLineRequest();
    request.setUsername("shippingLine");
    request.setPassword("12342434");
    request.setEmail("mail@gmail.com");
    request.setPhone("0965415415");
    request.setAddress("Ha Tay");
    request.setCompanyAddress("23sad");
    request.setCompanyCode("FOR");
    request.setCompanyDescription("ad2dce");
    request.setCompanyName("ShippingLine 1wes");
    request.setFax("32321123");
    request.setTin("23d235313");
    request.setWebsite("shippingLine.com");
    request.setFullname("Nguyen Van A");

    when(shippingLineService.createShippingLine(Mockito.any(ShippingLineRequest.class))).thenReturn(shippingLine);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/shipping-line").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.username").value("shippingLine")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getShippingLine_thenStatusOk_andReturnShippingLine() throws JsonProcessingException, Exception {
    // given
    when(shippingLineService.getShippingLine(Mockito.anyLong())).thenReturn(shippingLine);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/shipping-line/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("shippingLine")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getShippingLines_thenStatusOk_andReturnShippingLines() throws Exception {
    // given
    when(shippingLineService.getShippingLines(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/shipping-line").contentType(MediaType.APPLICATION_JSON).params(requestParams)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].username").value("shippingLine")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void editShippingLine_thenStatusOk_andReturnShippingLine() throws Exception {
    // given
    shippingLine.setEmail("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("email", "123456");
    when(shippingLineService.editShippingLine(Mockito.anyLong(), Mockito.anyMap())).thenReturn(shippingLine);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/shipping-line/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.email").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void deleteShippingLine_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/shipping-line/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa hãng tàu thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
