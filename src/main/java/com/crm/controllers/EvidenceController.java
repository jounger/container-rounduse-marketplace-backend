package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.crm.models.Evidence;
import com.crm.models.dto.EvidenceDto;
import com.crm.models.mapper.EvidenceMapper;
import com.crm.payload.request.EvidenceRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.EvidenceService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

  @Autowired
  private EvidenceService evidenceService;

  @Transactional
  @PostMapping("/contract/{id}")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  public ResponseEntity<?> createEvidence(@PathVariable("id") Long id, @Valid @RequestBody EvidenceRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Evidence evidence = evidenceService.createEvidence(id, username, request);
    EvidenceDto evidenceDto = EvidenceMapper.toEvidenceDto(evidence);
    return ResponseEntity.ok(evidenceDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/user")
  public ResponseEntity<?> getEvidencesByUser(@Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Evidence> pages = evidenceService.getEvidencesByUser(username, request);

    PaginationResponse<EvidenceDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Evidence> evidences = pages.getContent();
    List<EvidenceDto> evidencesDto = new ArrayList<>();
    evidences.forEach(evidence -> evidencesDto.add(EvidenceMapper.toEvidenceDto(evidence)));
    response.setContents(evidencesDto);

    return ResponseEntity.ok(response);
  }
  
  @PreAuthorize("hasRole('MODERATOR') or hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/contract/{id}")
  public ResponseEntity<?> getEvidencesByContract(@PathVariable("id") Long id, @Valid PaginationRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = userDetails.getId();

    Page<Evidence> pages = evidenceService.getEvidencesByContract(id, userId, request);

    PaginationResponse<EvidenceDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Evidence> evidences = pages.getContent();
    List<EvidenceDto> evidencesDto = new ArrayList<>();
    evidences.forEach(evidence -> evidencesDto.add(EvidenceMapper.toEvidenceDto(evidence)));
    response.setContents(evidencesDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/filter")
  public ResponseEntity<?> searchEvidences(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    Page<Evidence> pages = evidenceService.searchEvidences(request, search);
    PaginationResponse<EvidenceDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Evidence> evidences = pages.getContent();
    List<EvidenceDto> evidencesDto = new ArrayList<>();
    evidences.forEach(evidence -> evidencesDto.add(EvidenceMapper.toEvidenceDto(evidence)));
    response.setContents(evidencesDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editEvidence(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Evidence evidence = evidenceService.editEvidence(id, username, updates);
    EvidenceDto evidenceDto = EvidenceMapper.toEvidenceDto(evidence);
    return ResponseEntity.ok(evidenceDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteEvidence(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    evidenceService.removeEvidence(id, username);
    return ResponseEntity.ok(new MessageResponse("Evidence deleted successfully."));
  }
}
