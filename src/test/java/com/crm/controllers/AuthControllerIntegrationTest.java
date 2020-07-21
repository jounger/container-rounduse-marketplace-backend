package com.crm.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import com.crm.payload.request.SupplierRequest;
import com.crm.security.jwt.AuthEntryPointJwt;
import com.crm.security.jwt.JwtUntils;
import com.crm.security.services.UserDetailsServiceImpl;

@WebMvcTest(AuthController.class)
public class AuthControllerIntegrationTest extends IntegrationTest {

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

  @Test
  public void contextLoads() {

  }

  @Test
  public void givenForwarderRegister_whenSignup_thenStatus200() throws Exception {
    String uri = "/api/auth/signup";
    SupplierRequest request = new SupplierRequest();
    request.setUsername("ahalogistic");
    request.setPhone("0967390001");
    request.setEmail("ahalogistic@gmail.com");
    HashSet<String> roles = new HashSet<>();
    roles.add("FORWARDER");
    request.setRoles(roles);
    request.setAddress("Bac Ninh, Viet Nam");
    request.setPassword("123456");
    request.setWebsite("aha.com");
    request.setContactPerson("Nguyen Van A");
    request.setCompanyName("Aha Logistic");
    request.setCompanyCode("AHA");
    request.setCompanyDescription("Cong ty xuat khau");
    request.setCompanyAddress("KCN Yen Phong, Bac Ninh, Viet Nam");
    request.setTin("HYAO293");
    request.setFax("932093209");

    String inputJson = super.mapToJson(request);
    System.out.println("inputJson: " + inputJson);
    MvcResult mvcResult = super.mockMvc
        .perform(post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

    MockHttpServletResponse response = mvcResult.getResponse();

    int status = response.getStatus();
    assertThat(HttpStatus.CREATED.value()).isEqualTo(status);
  }
}
