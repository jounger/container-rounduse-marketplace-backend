package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;
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
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class UserControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(UserControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private UserService userService;

  PaginationRequest paginationRequest;

  List<User> users = new ArrayList<>();

  Page<User> pages;

  @BeforeEach
  public void setUp() {
    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "ADMIN" })
  public void whenGetUsers_then200() throws Exception {
    // CREATE USER
    User user1 = new User();
    user1.setId(1L);
    user1.setUsername("nguyenvanan");
    user1.setPassword("123456");
    user1.setPhone("0967390098");
    user1.setEmail("annvse@fpt.edu.vn");
    user1.setAddress("HN, Vietnam");
    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);
    user1.setRoles(roles);
    users.add(user1);
    pages = new PageImpl<User>(users);

    // CREATE PARAMS
    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    // CREATE MOCK RETURN
    when(userService.getUsers((Mockito.any(PaginationRequest.class)))).thenReturn(pages);

    // PERFORM
    MvcResult mvcResult = mockMvc
        .perform(get("/api/user").params(requestParams).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.data[0].username").value("nguyenvanan")).andReturn();

    // RESPONSE
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }
}
