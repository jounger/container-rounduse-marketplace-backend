package com.crm.controllers;

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
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.crm.models.Forwarder;
import com.crm.models.Role;
import com.crm.payload.request.SupplierRequest;
import com.crm.services.ForwarderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class AuthControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(AuthControllerIT.class);

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  ForwarderService forwarderService;

  Forwarder forwarder;

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
    // given
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

    when(forwarderService.createForwarder(Mockito.any(SupplierRequest.class))).thenReturn(forwarder);

    // when and then
    MvcResult mvcResult = mockMvc
        .perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.username").value("anvannguyen")).andReturn();
    
    // print response
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
