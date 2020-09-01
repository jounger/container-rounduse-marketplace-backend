package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import com.crm.enums.EnumFileType;
import com.crm.models.ContractDocument;
import com.crm.models.FileUpload;
import com.crm.models.Merchant;
import com.crm.payload.request.ContractDocumentRequest;
import com.crm.payload.request.FileUploadRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.ContractDocumentService;
import com.crm.services.FileUploadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class ContractDocumentControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(ContractDocumentControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private ContractDocumentService contractDocumentService;

  @MockBean
  private FileUploadService fileUploadService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<ContractDocument> pages;

  List<ContractDocument> contractDocuments;

  LinkedMultiValueMap<String, String> requestParams;

  ContractDocument contractDocument;

  @BeforeEach
  public void setUp() {

    Merchant merchant = new Merchant();
    merchant.setUsername("merchant");

    contractDocument = new ContractDocument();
    contractDocument.setId(1L);
    contractDocument.setStatus("PENDING");
    contractDocument.setSender(merchant);

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");

    List<ContractDocument> evidences = new ArrayList<ContractDocument>();
    evidences.add(contractDocument);
    pages = new PageImpl<ContractDocument>(evidences);
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void createContractDocument_thenStatusOk_andReturnContractDocument() throws JsonProcessingException, Exception {
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
    when(contractDocumentService.createContractDocument(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(ContractDocumentRequest.class))).thenReturn(contractDocument);

    MvcResult result = mockMvc
        .perform(
            multipart("/api/contract-document/contract/1").file(file).file(metadata).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.message").value("Thêm mới bằng chứng thành công")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void getContractDocumentsByContract_thenStatusOk_andReturnContractDocuments()
      throws JsonProcessingException, Exception {
    // given
    when(contractDocumentService.getContractDocumentsByContract(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(PaginationRequest.class))).thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/contract-document/contract/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].status").value("PENDING")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void searchContractDocuments_thenStatusOk_andReturnContractDocuments() throws Exception {
    // given
    String search = "status:PENDING";
    requestParams.add("search", search);
    when(contractDocumentService.searchContractDocuments(Mockito.any(PaginationRequest.class), Mockito.anyString()))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/contract-document/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].status").value("PENDING")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void editContractDocument_thenStatusOk_andReturnContractDocument() throws Exception {
    // given
    contractDocument.setStatus("ACCEPTED");
    Map<String, Object> updates = new HashMap<String, Object>();
    updates.put("status", "ACCEPTED");
    when(contractDocumentService.editContractDocument(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap()))
        .thenReturn(contractDocument);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/contract-document/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.status").value("ACCEPTED")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "forwarder", roles = { "FORWARDER" })
  void deleteContractDocument_thenStatusOk_AndReturnMessage() throws Exception {

    // when and then
    MvcResult result = mockMvc.perform(delete("/api/contract-document/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Xóa bằng chứng thành công"))
        .andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
