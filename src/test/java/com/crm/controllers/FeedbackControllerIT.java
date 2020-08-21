package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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

import com.crm.models.Feedback;
import com.crm.models.Forwarder;
import com.crm.models.Operator;
import com.crm.models.Report;
import com.crm.payload.request.FeedbackRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.FeedbackService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class FeedbackControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(FeedbackControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private FeedbackService feedbackService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Feedback> pages;

  List<Feedback> feedbacks;

  LinkedMultiValueMap<String, String> requestParams;

  Feedback feedback;

  @BeforeEach
  public void setUp() {
    Operator operator = new Operator();
    operator.setUsername("operator");
    Forwarder forwarder = new Forwarder();
    forwarder.setUsername("forwarder");
    
    Report report = new Report();
    report.setId(1L);

    feedback = new Feedback();
    feedback.setId(1L);
    feedback.setSatisfactionPoints(3);
    feedback.setMessage("abc");
    feedback.setRecipient(forwarder);
    feedback.setSender(operator);
    feedback.setReport(report);
    feedback.setSendDate(LocalDateTime.now());

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<Feedback> feedbacks = new ArrayList<Feedback>();
    feedbacks.add(feedback);
    pages = new PageImpl<Feedback>(feedbacks);
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void createFeedback_thenStatusOk_andReturnFeedback() throws JsonProcessingException, Exception {
    // given
    FeedbackRequest request = new FeedbackRequest();
    request.setMessage("abc");
    when(feedbackService.createFeedback(Mockito.anyLong(), Mockito.anyString(), Mockito.any(FeedbackRequest.class)))
        .thenReturn(feedback);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/feedback/report/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.sender.username").value("operator")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createFeedbackToModerator_thenStatusOk_andReturnFeedback() throws JsonProcessingException, Exception {
    // given
    FeedbackRequest request = new FeedbackRequest();
    request.setMessage("abc");
    requestParams.add("id", "1");
    requestParams.add("name", "forwarder");

    when(feedbackService.createFeedback(Mockito.anyLong(), Mockito.anyString(), Mockito.any(FeedbackRequest.class)))
        .thenReturn(feedback);

    // when and then
    MvcResult result = mockMvc
        .perform(post("/api/feedback").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.recipient.username").value("forwarder")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getFeedbacksByReport_thenStatusOk_andReturnFeedbacks() throws JsonProcessingException, Exception {
    // given
    when(feedbackService.getFeedbacksByReport(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/feedback/report/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].recipient.username").value("forwarder")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getFeedbacksByUser_thenStatusOk_andReturnFeedbacks() throws JsonProcessingException, Exception {
    // given
    when(feedbackService.getFeedbacksByUser(Mockito.anyString(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/feedback/user").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].sender.username").value("operator")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void editFeedback_thenStatusOk_andReturnFeedback() throws Exception {
    // given
    feedback.setMessage("123456");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("code", "123456");
    when(feedbackService.editFeedback(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(feedback);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/feedback/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.message").value("123456")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void deleteFeedback_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/feedback/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa phản hồi thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
