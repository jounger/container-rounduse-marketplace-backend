package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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

import com.crm.models.Role;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RoleRequest;
import com.crm.services.RoleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class RoleControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(RoleControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private RoleService roleService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Role> pages;

  List<Role> roles;

  LinkedMultiValueMap<String, String> requestParams;

  Role role;

  @BeforeEach
  public void setUp() {

    role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Role> roles = new ArrayList<Role>();
    roles.add(role);
    pages = new PageImpl<Role>(roles);
  }

  @Test
  @WithMockUser(username = "ADMIN", roles = { "ADMIN" })
  void createRole_thenStatusOk_andReturnRole() throws JsonProcessingException, Exception {
    // given
    RoleRequest request = new RoleRequest();
    request.setName("ROLE_MERCHANT");

    when(roleService.createRole(Mockito.any(RoleRequest.class))).thenReturn(role);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/role").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.name").value("ROLE_MERCHANT")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void getRoles_thenStatusOk_andReturnRoles() throws JsonProcessingException, Exception {
    // given
    when(roleService.getRoles(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/role").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].name").value("ROLE_MERCHANT")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "ADMIN", roles = { "ADMIN" })
  void editRole_thenStatusOk_andReturnRole() throws Exception {
    // given
    role.setName("ROLE_ABC");
    RoleRequest request = new RoleRequest();
    request.setName("ROLE_ABC");
    when(roleService.updateRole(Mockito.any(RoleRequest.class))).thenReturn(role);

    // when and then
    MvcResult result = mockMvc
        .perform(put("/api/role").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.name").value("ROLE_ABC")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "admin", roles = { "ADMIN" })
  void deleteRole_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/role/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Xóa vai trò thành công")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
