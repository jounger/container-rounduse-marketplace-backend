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

import com.crm.models.Evidence;
import com.crm.models.Merchant;
import com.crm.payload.request.EvidenceRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.EvidenceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class EvidenceControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(EvidenceControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private EvidenceService evidenceService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Evidence> pages;

  List<Evidence> evidences;

  LinkedMultiValueMap<String, String> requestParams;

  Evidence evidence;

  @BeforeEach
  public void setUp() {
    
    Merchant merchant = new Merchant();
    merchant.setUsername("merchant");

    evidence = new Evidence();
    evidence.setId(1L);
    evidence.setStatus("PENDING");
    evidence.setSender(merchant);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Evidence> evidences = new ArrayList<Evidence>();
    evidences.add(evidence);
    pages = new PageImpl<Evidence>(evidences);
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createEvidence_thenStatusOk_andReturnEvidence() throws JsonProcessingException, Exception {
    // given
    EvidenceRequest request = new EvidenceRequest();
    request.setSender("merchant");
    ;
    when(evidenceService.createEvidence(Mockito.anyLong(), Mockito.anyString(), Mockito.any(EvidenceRequest.class)))
        .thenReturn(evidence);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/evidence/contract/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.isValid").value(false)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getEvidencesByUser_thenStatusOk_andReturnEvidences() throws JsonProcessingException, Exception {
    // given
    when(evidenceService.getEvidencesByUser(Mockito.anyString(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/evidence/user").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].isValid").value("false")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getEvidencesByContract_thenStatusOk_andReturnEvidences() throws JsonProcessingException, Exception {
    // given
    when(evidenceService.getEvidencesByContract(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/evidence/contract/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].isValid").value("false")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void searchEvidences_thenStatusOk_andReturnEvidences() throws Exception {
    // given
    String search = "required:false";
    requestParams.add("search", search);
    when(evidenceService.searchEvidences(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/evidence/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].isValid").value("false")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editEvidence_thenStatusOk_andReturnEvidence() throws Exception {
    // given
    evidence.setStatus("ACCEPTED");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("isValid", "true");
    when(evidenceService.editEvidence(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(evidence);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/evidence/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.isValid").value("ACCEPTED")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deleteEvidence_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/evidence/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa bằng chứng thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
