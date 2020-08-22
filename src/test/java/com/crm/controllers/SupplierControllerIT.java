package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

import com.crm.models.Permission;
import com.crm.models.Role;
import com.crm.models.Supplier;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.SupplierService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class SupplierControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(SupplierControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private SupplierService supplierService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Supplier> pages;

  List<Supplier> suppliers;

  LinkedMultiValueMap<String, String> requestParams;

  Supplier supplier;

  @BeforeEach
  public void setUp() {

    supplier = new Supplier();
    supplier.setId(1L);
    supplier.setUsername("supplier");
    supplier.setStatus("PENDING");
    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    Collection<Permission> permissions = new ArrayList<Permission>();
    Permission permission = new Permission();
    permission.setId(1L);
    permission.setName("EDIT");
    role.setPermissions(permissions);

    supplier.setRoles(roles);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Supplier> suppliers = new ArrayList<Supplier>();
    suppliers.add(supplier);
    pages = new PageImpl<Supplier>(suppliers);
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getSupplier_thenStatusOk_andReturnSupplier() throws JsonProcessingException, Exception {
    // given
    when(supplierService.getSupplier(Mockito.anyString())).thenReturn(supplier);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/supplier/supplier").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("supplier")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getSuppliersByRole_thenStatusOk_andReturnSuppliers() throws Exception {
    // given
    when(supplierService.getSuppliersByRole(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/supplier/role").contentType(MediaType.APPLICATION_JSON).params(requestParams)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].username").value("supplier")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getSuppliersByStatus_thenStatusOk_andReturnSuppliers() throws Exception {
    // given
    when(supplierService.getSuppliersByStatus(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/supplier/status").contentType(MediaType.APPLICATION_JSON).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].username").value("supplier")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getSuppliers_thenStatusOk_andReturnSuppliers() throws Exception {
    // given
    when(supplierService.getSuppliers(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/supplier").contentType(MediaType.APPLICATION_JSON).params(requestParams)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].username").value("supplier")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "supplier", roles = { "FORWARDER" })
  void searchSuppliers_thenStatusOk_andReturnSuppliers() throws JsonProcessingException, Exception {
    // given
    String search = "username:supplier";
    requestParams.add("search", search);
    when(supplierService.searchSuppliers(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/supplier/filter").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].username").value("supplier")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void reviewRegister_thenStatusOk_andReturnSupplier() throws Exception {
    // given
    supplier.setStatus("ACTIVE");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("status", "ACTIVE");
    when(supplierService.editSupplier(Mockito.anyMap(), Mockito.anyLong())).thenReturn(supplier);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/supplier/register/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.message").value("Đơn đăng ký đã được chấp nhận")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void editSupplier_thenStatusOk_andReturnSupplier() throws Exception {
    // given
    supplier.setEmail("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("email", "123456");
    when(supplierService.editSupplier(Mockito.anyMap(), Mockito.anyLong())).thenReturn(supplier);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/supplier/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.email").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
