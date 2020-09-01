package com.crm.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import com.crm.enums.EnumFileType;
import com.crm.models.FileUpload;
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.ContractDocumentRequest;
import com.crm.payload.request.FileUploadRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ResetPasswordRequest;
import com.crm.services.FileUploadService;
import com.crm.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class UserControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(UserControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private FileUploadService fileUploadService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  List<User> users = new ArrayList<>();

  Page<User> pages;

  User user;

  @BeforeEach
  public void setUp() {

    logger.info("------------------------------------");
    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);

    // CREATE USER
    user = new User();
    user.setId(1L);
    user.setUsername("nguyenvanan");
    user.setPassword("123456");
    user.setPhone("0967390098");
    user.setEmail("annvse@fpt.edu.vn");
    user.setAddress("HN, Vietnam");
    Collection<Role> roles = new ArrayList<Role>();
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_FORWARDER");
    roles.add(role);
    user.setRoles(roles);
    users.add(user);
    pages = new PageImpl<User>(users);
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  public void whenGetUsers_then200() throws Exception {

    // given
    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    when(userService.getUsers((Mockito.any(PaginationRequest.class)))).thenReturn(pages);

    // when and then
    MvcResult mvcResult = mockMvc
        .perform(get("/api/user").params(requestParams).contentType(MediaType.APPLICATION_JSON_VALUE)).andDo(print())
        .andExpect(status().isOk()).andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.totalPages").value(1)).andReturn();

    // print response
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void searchUsersByUsername_thenStatusOkAndReturnUsers() throws Exception {
    String search = "username:moderator";

    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
    requestParams.add("search", search);

    when(userService.searchUsers(Mockito.any(PaginationRequest.class), Mockito.anyString())).thenReturn(pages);
    MvcResult mvcResult = mockMvc
        .perform(get("/api/user/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.totalPages").isNumber())
        .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.data[0].id").value(1)).andReturn();

    // RESPONSE
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());

  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void getResetPasswordToken_thenStatusOkAndReturnToken() throws Exception {
    // given
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setEmail("quyennvse04772@fpt.edu.vn");
    doNothing().when(userService).getResetPasswordToken(Mockito.anyString());

    // when and then
    MvcResult mvcResult = mockMvc
        .perform(post("/api/user/reset-password").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andReturn();

    // RESPONSE
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void isValidResetPasswordToken_thenStatusOkAndReturnTrue() throws Exception {
    // given
    ResetPasswordRequest request = new ResetPasswordRequest();
    String token = "213-9pd90f0q9jw0q3urjdslzmdoeq0-23-05q3r[pqawk13@#527";
    request.setToken(token);
    when(userService.isValidResetPasswordTolken(Mockito.anyString())).thenReturn(true);

    // when and then
    MvcResult mvcResult = mockMvc
        .perform(get("/api/user/reset-password").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data").value(true)).andReturn();

    // RESPONSE
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "moderator", roles = { "MODERATOR" })
  void resetPasswordByToken_thenStatusOkAndReturnSuccessMessage() throws Exception {
    // given
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setToken("quyennvse04772@fpt.edu.vn");
    request.setNewPassword("123456");
    doNothing().when(userService).resetPasswordByToken(Mockito.anyString(), Mockito.anyString());

    // when and then
    MvcResult mvcResult = mockMvc
        .perform(patch("/api/user/reset-password").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Thay đổi mật khẩu thành công")).andReturn();

    // RESPONSE
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void uploadProfileImage_thenStatusOk_andReturnUser() throws JsonProcessingException, Exception {
    // given
    ContractDocumentRequest request = new ContractDocumentRequest();
    request.setSender("merchant");
    // MultipartFile file = null;
    MockMultipartFile file = new MockMultipartFile("file", "contract.pdf", MediaType.APPLICATION_PDF_VALUE,
        "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

    ObjectMapper objectMapper = new ObjectMapper();

    MockMultipartFile metadata = new MockMultipartFile("request", "request", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    fileUploadRequest.setFile(file);
    fileUploadRequest.setType(EnumFileType.DOCUMENT.name());
    request.setDocumentPath("2sva/2de2/ads2s");

    FileUpload fileUpload = new FileUpload();
    fileUpload.setId(1L);
    fileUpload.setName("name");
    fileUpload.setOriginName("originName");
    fileUpload.setPath("path");

    when(fileUploadService.createFileUpload(Mockito.anyString(), Mockito.any(FileUploadRequest.class)))
        .thenReturn(fileUpload);
    when(userService.editUser(Mockito.anyString(), Mockito.anyMap())).thenReturn(user);

    MvcResult result = mockMvc
        .perform(
            multipart("/api/user/upload-profile").file(file).file(metadata).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Thay đổi ảnh đại diện thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }
}