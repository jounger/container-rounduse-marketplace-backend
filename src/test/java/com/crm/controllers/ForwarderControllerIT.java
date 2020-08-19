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

import com.crm.models.Forwarder;
import com.crm.models.Permission;
import com.crm.models.Role;
import com.crm.payload.request.ForwarderRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.ForwarderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class ForwarderControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(ForwarderControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private ForwarderService forwarderService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Forwarder> pages;

  List<Forwarder> forwarders;

  LinkedMultiValueMap<String, String> requestParams;

  Forwarder forwarder;

  @BeforeEach
  public void setUp() {

    forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");
    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    Collection<Permission> permissions = new ArrayList<Permission>();
    Permission permission = new Permission();
    permission.setId(1L);
    permission.setName("EDIT");
    role.setPermissions(permissions);

    forwarder.setRoles(roles);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Forwarder> forwarders = new ArrayList<Forwarder>();
    forwarders.add(forwarder);
    pages = new PageImpl<Forwarder>(forwarders);
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void createForwarder_thenStatusOk_andReturnForwarder() throws JsonProcessingException, Exception {
    // given
    ForwarderRequest request = new ForwarderRequest();
    request.setUsername("forwarder");
    request.setPassword("12342434");
    request.setEmail("mail@gmail.com");
    request.setPhone("0965415415");
    request.setAddress("Ha Tay");
    request.setCompanyAddress("23sad");
    request.setCompanyCode("FOR");
    request.setCompanyDescription("ad2dce");
    request.setCompanyName("Forwarder 1wes");
    request.setFax("32321123");
    request.setTin("23d235313");
    request.setWebsite("forwarder.com");

    when(forwarderService.createForwarder(Mockito.any(ForwarderRequest.class))).thenReturn(forwarder);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/forwarder").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.username").value("forwarder")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getForwarder_thenStatusOk_andReturnForwarder() throws JsonProcessingException, Exception {
    // given
    when(forwarderService.getForwarder(Mockito.anyLong())).thenReturn(forwarder);

    // when and then
    MvcResult result = mockMvc.perform(get("/api/forwarder/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("forwarder")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getForwarders_thenStatusOk_andReturnForwarders() throws Exception {
    // given
    when(forwarderService.getForwarders(Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/forwarder").contentType(MediaType.APPLICATION_JSON).params(requestParams)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].username").value("forwarder")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getForwardersByOutbound_thenStatusOk_andReturnForwarders() throws JsonProcessingException, Exception {
    // given
    when(forwarderService.findForwardersByOutbound(Mockito.anyLong(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/forwarder/outbound/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].username").value("forwarder")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void editForwarder_thenStatusOk_andReturnForwarder() throws Exception {
    // given
    forwarder.setEmail("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("email", "123456");
    when(forwarderService.editForwarder(Mockito.anyLong(), Mockito.anyMap())).thenReturn(forwarder);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/forwarder/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.email").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void deleteForwarder_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/forwarder/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa chủ xe thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
