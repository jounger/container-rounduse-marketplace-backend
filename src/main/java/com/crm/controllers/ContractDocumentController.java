package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crm.common.SuccessMessage;
import com.crm.enums.EnumFileType;
import com.crm.models.ContractDocument;
import com.crm.models.FileUpload;
import com.crm.models.dto.ContractDocumentDto;
import com.crm.models.mapper.ContractDocumentMapper;
import com.crm.payload.request.ContractDocumentRequest;
import com.crm.payload.request.FileUploadRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ContractDocumentService;
import com.crm.services.FileUploadService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/evidence")
public class ContractDocumentController {

  private static final Logger logger = LoggerFactory.getLogger(ContractDocumentController.class);

  @Autowired
  private ContractDocumentService contractDocumentService;

  @Autowired
  private FileUploadService fileUploadService;

  @Transactional
  @PostMapping("/contract/{id}")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  public ResponseEntity<?> createContractDocument(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    FileUploadRequest fileUploadRequest = new FileUploadRequest();
    fileUploadRequest.setFile(file);
    fileUploadRequest.setType(EnumFileType.DOCUMENT.name());

    FileUpload fileUpload = fileUploadService.createFileUpload(username, fileUploadRequest);
    String filePath = fileUpload.getPath() + fileUpload.getName();

    ContractDocumentRequest request = new ContractDocumentRequest();
    request.setDocumentPath(filePath);

    ContractDocument contractDocument = contractDocumentService.createContractDocument(id, username, request);
    ContractDocumentDto contractDocumentDto = ContractDocumentMapper.toContractDocumentDto(contractDocument);

    // Set default response body
    DefaultResponse<ContractDocumentDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_EVIDENCE_SUCCESSFULLY);
    defaultResponse.setData(contractDocumentDto);

    logger.info("User {} createContractDocument with request: {}", username, request.toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/user")
  public ResponseEntity<?> getContractDocumentsByUser(@Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<ContractDocument> pages = contractDocumentService.getContractDocumentsByUser(username, request);

    PaginationResponse<ContractDocumentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ContractDocument> contractDocuments = pages.getContent();
    List<ContractDocumentDto> evidencesDto = new ArrayList<>();
    contractDocuments.forEach(evidence -> evidencesDto.add(ContractDocumentMapper.toContractDocumentDto(evidence)));
    response.setContents(evidencesDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/contract/{id}")
  public ResponseEntity<?> getContractDocumentsByContract(@PathVariable("id") Long id, @Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<ContractDocument> pages = contractDocumentService.getContractDocumentsByContract(id, username, request);

    PaginationResponse<ContractDocumentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ContractDocument> contractDocuments = pages.getContent();
    List<ContractDocumentDto> evidencesDto = new ArrayList<>();
    contractDocuments.forEach(evidence -> evidencesDto.add(ContractDocumentMapper.toContractDocumentDto(evidence)));
    response.setContents(evidencesDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/filter")
  public ResponseEntity<?> searchContractDocuments(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    Page<ContractDocument> pages = contractDocumentService.searchContractDocuments(request, search);
    PaginationResponse<ContractDocumentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ContractDocument> contractDocuments = pages.getContent();
    List<ContractDocumentDto> evidencesDto = new ArrayList<>();
    contractDocuments.forEach(evidence -> evidencesDto.add(ContractDocumentMapper.toContractDocumentDto(evidence)));
    response.setContents(evidencesDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editContractDocument(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    ContractDocument contractDocument = contractDocumentService.editContractDocument(id, username, updates);
    ContractDocumentDto contractDocumentDto = ContractDocumentMapper.toContractDocumentDto(contractDocument);

    // Set default response body
    DefaultResponse<ContractDocumentDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_EVIDENCE_SUCCESSFULLY);
    defaultResponse.setData(contractDocumentDto);

    logger.info("User {} editContractDocument from id {} with request: {}", username, id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteContractDocument(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    contractDocumentService.removeContractDocument(id, username);

    // Set default response body
    DefaultResponse<ContractDocumentDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_EVIDENCE_SUCCESSFULLY);

    logger.info("User {} deleteContractDocument with id {}", username, id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
