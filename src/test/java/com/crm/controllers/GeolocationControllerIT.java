package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import com.crm.models.Geolocation;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.GeolocationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class GeolocationControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(GeolocationControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private GeolocationService geolocationService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Geolocation> pages;

  List<Geolocation> geolocations;

  LinkedMultiValueMap<String, String> requestParams;

  Geolocation geolocation;

  @BeforeEach
  public void setUp() {

    geolocation = new Geolocation();
    geolocation.setId(1L);
    geolocation.setLatitude("CT12");
    geolocation.setLongitude("des");
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editGeolocation_thenStatusOk_andReturnGeolocation() throws Exception {
    // given
    geolocation.setLatitude("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("latitute", "123456");
    when(geolocationService.editGeolocation(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(geolocation);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/geolocation/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.latitude").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }
  
  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void editGeolocation_thenStatusForbidden_andReturnMessage() throws Exception {
    // given
    geolocation.setLatitude("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("latitute", "123456");
    when(geolocationService.editGeolocation(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(geolocation);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/geolocation/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isForbidden()).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
