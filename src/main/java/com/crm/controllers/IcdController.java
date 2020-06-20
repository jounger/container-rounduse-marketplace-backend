package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Icd;
import com.crm.models.dto.IcdDto;
import com.crm.models.mapper.IcdMapper;
import com.crm.payload.request.IcdRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.IcdService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/icd")
public class IcdController {

  @Autowired
  private IcdService icdService;

  @GetMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getConsignments(@Valid PaginationRequest request) {

    Page<Icd> pages = icdService.getIcds(request);
    PaginationResponse<IcdDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Icd> icds = pages.getContent();
    List<IcdDto> icdsDto = new ArrayList<>();
    icds.forEach(icd -> icdsDto.add(IcdMapper.toIcdDto(icd)));
    response.setContents(icdsDto);

    return ResponseEntity.ok(response);

  }

  @PostMapping("")
//  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> createIcd(@Valid @RequestBody IcdRequest request) {
    icdService.saveIcd(request);
    return ResponseEntity.ok(new MessageResponse("ICD created successfully"));
  }
  
  @Transactional
  @DeleteMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> removeIcd(@Valid @RequestBody IcdRequest request){       
    icdService.deleteIcd(request.getId());
    return ResponseEntity.ok(new MessageResponse("Icd has remove successfully"));
  }
  
  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> editConsignment(@Valid @RequestBody IcdRequest request){
    icdService.editIcd(request);
    return ResponseEntity.ok(new MessageResponse("Icd has update successfully"));
  }
  
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getConsignment(@PathVariable Long id){
    Icd icd = icdService.getIcdById(id);
    IcdDto icdDto = new IcdDto();
    icdDto = IcdMapper.toIcdDto(icd);
    return ResponseEntity.ok(icdDto);
  }

}
