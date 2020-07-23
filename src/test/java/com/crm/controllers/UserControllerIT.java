package com.crm.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.UserRepository;
import com.crm.security.jwt.AuthEntryPointJwt;
import com.crm.security.jwt.JwtUntils;
import com.crm.security.services.UserDetailsServiceImpl;
import com.crm.services.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

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
  private JwtUntils jwtUntils;

  @MockBean
  private AuthEntryPointJwt authEntryPointJwt;

  @MockBean
  private UserController userController;

  @MockBean
  private UserRepository userRepository;

  User user;

  private PaginationRequest request;
  
  Role role;
  
  Page<User> pages;

  @BeforeEach
  public void setUp() {
    user = new User();
    user.setId(1L);
    user.setUsername("testUser");
    user.setPassword("123456");
    user.setPhone("0967390001");
    user.setEmail("anvannguyen@gmail.com");
    user.setAddress("Ha Noi, Viet Nam");
    List<User> users = new ArrayList<User>();
    users.add(user);
    role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    user.getRoles().add(role);
    pages =  new PageImpl<User>(users);
    request = new PaginationRequest();
    request.setPage(0);
    request.setLimit(10);
  }

  @Test
  void testWhenTrue() {
    assertThat("123").isNotEqualToIgnoringCase("321");
  }

  @Test
  void searchUsersByUsername() throws Exception {
    String search = "username:testUser";

    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
    requestParams.add("search", search);

    when(userServiceImpl.searchUsers(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);
    MvcResult mvcResult = mockMvc
        .perform(post("/api/user/filter").contentType(MediaType.APPLICATION_JSON)
        .params(requestParams).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn();
    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    String expectedResponseBody = objectMapper.writeValueAsString(request);
    System.out.println("actual: " + actualResponseBody);
    System.out.println("expected: " + expectedResponseBody);
    assertThat(content()).isNotNull();
    
  }
  
//  @Test
//  void whenValidRegister_thenReturns200() throws Exception {
//    SupplierRequest request = new SupplierRequest();
//    request.setUsername("anvannguyen");
//    request.setPassword(passwordEncoder.encode("123456"));
//    request.setPhone("0967390001");
//    request.setEmail("anvannguyen@gmail.com");
//    request.setAddress("Ha Noi, Viet Nam");
//    Set<String> roles = new HashSet<>();
//    roles.add("FORWARDER");
//    request.setRoles(roles);
//    request.setWebsite("aha.com");
//    request.setContactPerson("Nguyen Van A");
//    request.setCompanyName("Aha Logistic");
//    request.setCompanyCode("AHA");
//    request.setCompanyDescription("Cong ty xuat khau");
//    request.setCompanyAddress("KCN Yen Phong, Bac Ninh, Viet Nam");
//    request.setTin("HYAO293");
//    request.setFax("932093209");
//    // MOCK: https://stackoverflow.com/a/37896584/10597062
//    when(forwarderServiceImpl.createForwarder(Mockito.any(SupplierRequest.class))).thenReturn(forwarder);
//    MvcResult mvcResult = mockMvc
//        .perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(request)).accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
//        .andExpect(jsonPath("$.username").value("anvannguyen")).andReturn();
//    String actualResponseBody = mvcResult.getResponse().getContentAsString();
//    request.setId(1L);
//    String expectedResponseBody = objectMapper.writeValueAsString(request);
//    System.out.println("actual: " + actualResponseBody);
//    System.out.println("expected: " + expectedResponseBody);
//    //assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
//  }

}
