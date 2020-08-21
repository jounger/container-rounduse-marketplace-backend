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

import com.crm.models.Operator;
import com.crm.models.Permission;
import com.crm.models.Role;
import com.crm.payload.request.OperatorRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.OperatorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class OperatorControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(OperatorControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private OperatorService operatorService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Operator> pages;

  List<Operator> operators;

  LinkedMultiValueMap<String, String> requestParams;

  Operator operator;

  @BeforeEach
  public void setUp() {

    operator = new Operator();
    operator.setId(1L);
    operator.setUsername("operator");

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MODERATOR");
    Collection<Permission> permissions = new ArrayList<Permission>();
    Permission permission = new Permission();
    permission.setId(1L);
    permission.setName("EDIT");
    role.setPermissions(permissions);

    operator.setRoles(roles);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Operator> operators = new ArrayList<Operator>();
    operators.add(operator);
    pages = new PageImpl<Operator>(operators);
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void createOperator_thenStatusOk_andReturnOperator() throws JsonProcessingException, Exception {
    // given
    OperatorRequest request = new OperatorRequest();
    request.setUsername("operator");
    request.setPassword("12342434");
    request.setEmail("mail@gmail.com");
    request.setPhone("0965415415");
    request.setAddress("Ha Tay");
    request.setFullname("Van A");
    when(operatorService.createOperator(Mockito.any(OperatorRequest.class))).thenReturn(operator);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/operator").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.username").value("operator")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getOperator_thenStatusOk_andReturnOperator() throws JsonProcessingException, Exception {
    // given
    when(operatorService.getOperatorById(Mockito.anyLong())).thenReturn(operator);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/operator/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("operator")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getOperatorByUsername_thenStatusOk_andReturnOperator() throws JsonProcessingException, Exception {
    // given
    requestParams = new LinkedMultiValueMap<String, String>();
    requestParams.add("username", "moderator");
    when(operatorService.getOperatorByUsername(Mockito.anyString())).thenReturn(operator);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/operator/username").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("operator")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getOperators_thenStatusOk_andReturnOperators() throws Exception {
    when(operatorService.getOperators(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/operator").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].username").value("operator")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void editOperator_thenStatusOk_andReturnOperator() throws Exception {
    // given
    operator.setFullname("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("fullName", "123456");
    when(operatorService.editOperator(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(operator);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/operator/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.fullname").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void deleteOperator_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/operator/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Xóa quản trị viên thành công")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
