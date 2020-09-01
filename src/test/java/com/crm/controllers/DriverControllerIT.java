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

import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Permission;
import com.crm.models.Role;
import com.crm.payload.request.DriverRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.DriverService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class DriverControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(DriverControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private DriverService driverService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Driver> pages;

  List<Driver> drivers;

  LinkedMultiValueMap<String, String> requestParams;

  Driver driver;

  @BeforeEach
  public void setUp() {

    driver = new Driver();
    driver.setId(1L);
    driver.setUsername("driver");

    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_DRIVER");
    Collection<Permission> permissions = new ArrayList<Permission>();
    Permission permission = new Permission();
    permission.setId(1L);
    permission.setName("EDIT");
    role.setPermissions(permissions);

    driver.setRoles(roles);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(2L);
    forwarder.setUsername("forwarder");

    driver.setForwarder(forwarder);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Driver> drivers = new ArrayList<Driver>();
    drivers.add(driver);
    pages = new PageImpl<Driver>(drivers);
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createDriver_thenStatusOk_andReturnDriver() throws JsonProcessingException, Exception {
    // given
    DriverRequest request = new DriverRequest();
    request.setUsername("driver");
    request.setPassword("12342434");
    request.setEmail("mail@gmail.com");
    request.setPhone("0965415415");
    request.setAddress("Ha Tay");
    request.setFullname("Van A");
    request.setDriverLicense("1sd3s3ssad");
    when(driverService.createDriver(Mockito.anyString(), Mockito.any(DriverRequest.class))).thenReturn(driver);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/driver/forwarder").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.username").value("driver")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getDriver_thenStatusOk_andReturnDriver() throws JsonProcessingException, Exception {
    // given
    when(driverService.getDriver(Mockito.anyLong())).thenReturn(driver);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/driver/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("driver")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getDriverByUsername_thenStatusOk_andReturnDriver() throws JsonProcessingException, Exception {
    // given
    requestParams = new LinkedMultiValueMap<String, String>();
    requestParams.add("username", "driver");
    when(driverService.getDriverByUsername(Mockito.anyString())).thenReturn(driver);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/driver").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("driver")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getDrivers_thenStatusOk_andReturnDrivers() throws Exception {
    when(driverService.getDrivers(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/driver").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].username").value("driver")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editDriver_thenStatusOk_andReturnDriver() throws Exception {
    // given
    driver.setFullname("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("fullName", "123456");
    when(driverService.editDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(driver);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/driver/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.fullname").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deleteDriver_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/driver/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa lái xe thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
