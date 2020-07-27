package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.UserRepository;
import com.crm.security.jwt.AuthEntryPointJwt;
import com.crm.security.jwt.JwtUntils;
import com.crm.security.services.UserDetailsImpl;
import com.crm.security.services.UserDetailsServiceImpl;
import com.crm.services.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserServiceImpl userServiceImpl;

  @MockBean
  private UserDetailsServiceImpl userDetailsService;

  @MockBean
  private AuthenticationManager authenticationManager;

  @MockBean
  private JwtUntils jwtUntils;

  @MockBean
  private AuthEntryPointJwt authEntryPointJwt;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private PasswordEncoder passwordEncoder;

  User user;

  private PaginationRequest request;

  Role role;

  Page<User> pages;

  @Autowired
  WebApplicationContext ctx;

  @Autowired
  private FilterChainProxy springSecurityFilterChain;

  String idToken;

  @BeforeEach
  public void setUp() {
    System.out.println("------------------");
    user = new User();
    user.setId(1L);
    user.setUsername("moderator");
    user.setPassword(passwordEncoder.encode("123456"));
    user.setPhone("0967390001");
    user.setEmail("anvannguyen@gmail.com");
    user.setAddress("Ha Noi, Viet Nam");
    user.setStatus("ACCEPTED");
    List<User> users = new ArrayList<User>();
    users.add(user);
    role = new Role();
    role.setId(1L);
    role.setName("ROLE_MODERATOR");
    user.getRoles().add(role);
    pages = new PageImpl<User>(users);
    request = new PaginationRequest();
    request.setPage(0);
    request.setLimit(10);

    mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(springSecurityFilterChain).build();
    // #formatter:off
    UserDetailsServiceImpl userDetailsServiceImpl = null;
    userDetailsServiceImpl = ctx.getBean(UserDetailsServiceImpl.class);
    final Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    idToken = jwtUntils.generateJwtToken(authentication);
    System.out.println("Id token: " + idToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    System.out.println("principal:" + SecurityContextHolder.getContext().getAuthentication().getPrincipal());

  }

  @Test
  void searchUsersByUsername() throws Exception {
    String search = "username:moderator";

    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
    requestParams.add("search", search);

    when(userServiceImpl.searchUsers(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);
    MvcResult mvcResult = mockMvc
        .perform(get("/api/user/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.totalPages").isNumber())
        .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.data[0].id").value(1)).andReturn();
    String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
    String expectedResponseBody = objectMapper.writeValueAsString(request);
    System.out.println("actual: " + actualResponseBody);
    System.out.println("expected: " + expectedResponseBody);
  }

  @Test
  @WithMockUser(username = "admin", authorities = { "MODERATOR" })
  void getUsers() throws Exception {

    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
    requestParams.add("status", "PENDING");

    when(userServiceImpl.getUsers(Mockito.any(PaginationRequest.class))).thenReturn(pages);
    mockMvc
        .perform(get("/api/user").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.data[0].status").value("PENDING")).andReturn();
  }

  @Test
  void changeStatus() throws Exception {
    Map<String, Object> updates = new HashMap<>();
    updates.put("status", "ACCEPTED");

    when(userServiceImpl.changeStatus(Mockito.anyLong(), Mockito.anyMap())).thenReturn(user);
    String token = "Bearer " + idToken;
    MvcResult mvcResult = mockMvc
        .perform(patch("/api/user/1").contentType(MediaType.APPLICATION_JSON).header("Authorization", token)
            .content(objectMapper.writeValueAsString(updates)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn();
    System.out.println("------------");
    System.out.println(mvcResult.getResponse().getRedirectedUrl());
    System.out.println(mvcResult.getResponse().getIncludedUrl());
    System.out.println(mvcResult.getResponse().getForwardedUrl());
    String actualResponseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
    String expectedResponseBody = objectMapper.writeValueAsString(request);
    System.out.println("actual: " + actualResponseBody);
    System.out.println("expected: " + expectedResponseBody);

  }

}
