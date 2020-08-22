package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.crm.services.SupplyService;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class SupplyControllerIT {
  
  private static final Logger logger = LoggerFactory.getLogger(SupplyControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private SupplyService supplyService;
  
  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void existByCode_returnStatus200_AndTrue() throws Exception{
    //given
    when(supplyService.existsByCode(Mockito.anyString())).thenReturn(true);
    
    // when and then
    MvcResult result = mockMvc.perform(get("/api/supply/q2dasd2w").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data").value(true)).andReturn();
    
    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
