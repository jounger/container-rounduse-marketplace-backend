package com.crm.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.crm.models.Forwarder;
import com.crm.models.Role;
import com.crm.payload.request.SignInRequest;
import com.crm.payload.request.SupplierRequest;
import com.crm.security.jwt.AuthEntryPointJwt;
import com.crm.security.jwt.JwtUntils;
import com.crm.security.services.UserDetailsServiceImpl;
import com.crm.services.impl.ForwarderServiceImpl;
import com.crm.services.impl.MerchantServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AuthController.class)
public class AuthControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AuthenticationManager authenticationManager;

  @MockBean
  private UserDetailsServiceImpl userDetailsService;

  @MockBean
  private JwtUntils jwtUntils;

  @MockBean
  private AuthEntryPointJwt authEntryPointJwt;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  ForwarderServiceImpl forwarderServiceImpl;
  
  @MockBean
  MerchantServiceImpl merchantServiceImpl;
  
  @MockBean
  private AuthController authController;

  Forwarder forwarder;
  
  Authentication authentication;
  
  String password;

  @BeforeEach
  void setUp() {
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("anvannguyen");
    password = passwordEncoder.encode("123456");
    forwarder.setPassword(password);
    forwarder.setPhone("0967390001");
    forwarder.setEmail("anvannguyen@gmail.com");
    forwarder.setAddress("Ha Noi, Viet Nam");
    Collection<Role> roles = new ArrayList<>();
    roles.add(role);
    forwarder.setRoles(roles);
    forwarder.setWebsite("aha.com");
    forwarder.setContactPerson("Nguyen Van A");
    forwarder.setCompanyName("Aha Logistic");
    forwarder.setCompanyCode("AHA");
    forwarder.setCompanyDescription("Cong ty xuat khau");
    forwarder.setCompanyAddress("KCN Yen Phong, Bac Ninh, Viet Nam");
    forwarder.setTin("HYAO293");
    forwarder.setFax("932093209");
  }

  @Test
  void whenValidRegister_thenReturns200() throws Exception {
    SupplierRequest request = new SupplierRequest();
    request.setUsername("anvannguyen");
    request.setPassword("123456");
    request.setPhone("0967390001");
    request.setEmail("anvannguyen@gmail.com");
    request.setAddress("Ha Noi, Viet Nam");
    Set<String> roles = new HashSet<>();
    roles.add("FORWARDER");
    request.setRoles(roles);
    request.setWebsite("aha.com");
    request.setContactPerson("Nguyen Van A");
    request.setCompanyName("Aha Logistic");
    request.setCompanyCode("AHA");
    request.setCompanyDescription("Cong ty xuat khau");
    request.setCompanyAddress("KCN Yen Phong, Bac Ninh, Viet Nam");
    request.setTin("HYAO293");
    request.setFax("932093209");
    // MOCK: https://stackoverflow.com/a/37896584/10597062
    when(forwarderServiceImpl.createForwarder(Mockito.any(SupplierRequest.class))).thenReturn(forwarder);
    MvcResult mvcResult = mockMvc
        .perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("anvannguyen")).andReturn();
    String actualResponseBody = mvcResult.getResponse().getContentAsString();
    request.setId(1L);
    String expectedResponseBody = objectMapper.writeValueAsString(request);
    System.out.println("actual: " + actualResponseBody);
    System.out.println("expected: " + expectedResponseBody);
    //assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }
  
  @Test
  void whenValidSignin_thenReturns200() throws Exception {
    SignInRequest request = new SignInRequest();
    request.setUsername("anvannguyen");
    request.setPassword("123456");
    authentication.setAuthenticated(true);
    
    // MOCK: https://stackoverflow.com/a/37896584/10597062
    when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
    MvcResult mvcResult = mockMvc
        .perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andReturn();
    String expectedResponseBody = objectMapper.writeValueAsString(request);
    assertThat(authentication.isAuthenticated()).isEqualTo(true);
    //assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
  }
}
