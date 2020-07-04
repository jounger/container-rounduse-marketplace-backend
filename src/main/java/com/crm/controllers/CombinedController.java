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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Combined;
import com.crm.models.dto.CombinedDto;
import com.crm.models.mapper.CombinedMapper;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.PaginationResponse;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.CombinedService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/combined")
public class CombinedController {

  @Autowired
  private CombinedService combinedService;
  
  /*
  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @PostMapping("")
  public ResponseEntity<?> createCombineddingDocument(@Valid @RequestBody CombinedRequest request) {
     Combined combined = combinedService.createCombined(request);
     CombinedDto combinedDto = CombinedMapper.toCombinedDto(combined);
     return ResponseEntity.ok(combinedDto);
  }
  */

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getCombined(@PathVariable Long id) {
    Combined combined = combinedService.getCombined(id);
    CombinedDto combinedDto = CombinedMapper.toCombinedDto(combined);
    return ResponseEntity.ok(combinedDto);
  }
  
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/user")
  public ResponseEntity<?> getCombinedsByUser(@Valid PaginationRequest request) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long id = userDetails.getId();
    
    Page<Combined> pages = combinedService.getCombinedsByUser(id, request);
    
    PaginationResponse<CombinedDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Combined> combineds = pages.getContent();
    List<CombinedDto> combinedsDto = new ArrayList<>();
    combineds.forEach(
        combined -> combinedsDto.add(CombinedMapper.toCombinedDto(combined)));
    response.setContents(combinedsDto);

    return ResponseEntity.ok(response);
  }

  /*
  @PreAuthorize("hasRole('MODERATOR')")
  @GetMapping("")
  public ResponseEntity<?> getCombineds(@Valid PaginationRequest request) {

    Page<Combined> pages = combinedService.getCombineds(request);

    PaginationResponse<CombinedDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Combined> combineds = pages.getContent();
    List<CombinedDto> combinedsDto = new ArrayList<>();
    combineds.forEach(
        combined -> combinedsDto.add(CombinedMapper.toCombinedDto(combined)));
    response.setContents(combinedsDto);

    return ResponseEntity.ok(response);
  }
  */
  
  @Transactional
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT') or hasRole('DRIVER')")
  @PutMapping("")
  public ResponseEntity<?> updateCombined(@Valid @RequestBody CombinedRequest request) {
     Combined combined = combinedService.updateCombined(request);     
     CombinedDto combinedDto = CombinedMapper.toCombinedDto(combined);
     return ResponseEntity.ok(combinedDto);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT') or hasRole('DRIVER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editCombined(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    Combined Combined = combinedService.editCombined(id, updates);
    CombinedDto CombinedDto = CombinedMapper.toCombinedDto(Combined);
    return ResponseEntity.ok(CombinedDto);
  }

  /*
  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> removeCombined(@PathVariable Long id) {
    combinedService.removeCombined(id);
    return ResponseEntity.ok(new MessageResponse("Combinedding document deleted successfully."));
  }
  */
}
