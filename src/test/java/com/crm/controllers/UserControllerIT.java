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

import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class UserControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(UserControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  List<User> users = new ArrayList<>();

  Page<User> pages;

  User user;

  @BeforeEach
  public void setUp() {

    logger.info("------------------------------------");
    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);

    // CREATE USER
    user = new User();
    user.setId(1L);
    user.setUsername("nguyenvanan");
    user.setPassword("123456");
    user.setPhone("0967390098");
    user.setEmail("annvse@fpt.edu.vn");
    user.setAddress("HN, Vietnam");
    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);
    user.setRoles(roles);
    users.add(user);
    pages = new PageImpl<User>(users);
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  public void whenGetUsers_then200() throws Exception {

    // given
    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    when(userService.getUsers((Mockito.any(PaginationRequest.class)))).thenReturn(pages);

    // when and then
    MvcResult mvcResult = mockMvc
        .perform(get("/api/user").params(requestParams).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.totalPages").value(1)).andReturn();

    // print response
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void searchUsersByUsername_thenStatusOkAndReturnUsers() throws Exception {
    String search = "username:moderator";

    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
    requestParams.add("search", search);

    when(userService.searchUsers(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);
    MvcResult mvcResult = mockMvc
        .perform(get("/api/user/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.totalPages").isNumber())
        .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.data[0].id").value(1)).andReturn();

    // RESPONSE
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());

  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void changeStatus() throws Exception {
    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "ACCEPTED");
    user.setStatus("ACCEPTED");

    when(userService.searchUsers(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);
    MvcResult mvcResult = mockMvc
        .perform(patch("/api/user/1").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updates)).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$..status").value("ACCEPTED")).andReturn();

    // RESPONSE
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }
}