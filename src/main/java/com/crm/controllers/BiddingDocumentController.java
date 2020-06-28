package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.BiddingDocument;
import com.crm.models.dto.BiddingDocumentDto;
import com.crm.models.mapper.BiddingDocumentMapper;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.BiddingDocumentService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bidding-document")
public class BiddingDocumentController {
  
  @Autowired
  private BiddingDocumentService biddingDocumentService;

  @PreAuthorize("hasRole('MERCHANT')")
  @PostMapping("")
  public ResponseEntity<?> createBiddingDocument(@Valid @RequestBody BiddingDocumentRequest request) {
     BiddingDocument biddingDocument = biddingDocumentService.createBiddingDocument(request);     
     return ResponseEntity.ok(biddingDocument);
  } 
  
  @PreAuthorize("hasRole('MERCHANT')")
  @GetMapping("/merchant/{id}")
  public ResponseEntity<?> getBiddingDocumentsByMerchant(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<BiddingDocument> pages = biddingDocumentService.getBiddingDocumentsByMerchant(id, request);

    PaginationResponse<BiddingDocumentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<BiddingDocument> biddingDocuments = pages.getContent();
    List<BiddingDocumentDto> biddingDocumentsDto = new ArrayList<>();
    biddingDocuments.forEach(
        biddingDocument -> biddingDocumentsDto.add(BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument)));
    response.setContents(biddingDocumentsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getBiddingDocument(@PathVariable Long id) {
    BiddingDocument biddingDocument = biddingDocumentService.getBiddingDocument(id);
    BiddingDocumentDto biddingDocumentDto = BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument);
    return ResponseEntity.ok(biddingDocumentDto);
  }

  @PreAuthorize("hasRole('MERCHANT')")
  @PutMapping("")
  public ResponseEntity<?> updateBiddingDocument(@Valid @RequestBody BiddingDocumentRequest request) {
     BiddingDocument biddingDocument = biddingDocumentService.updateBiddingDocument(request);     
     BiddingDocumentDto biddingDocumentDto = BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument);
     return ResponseEntity.ok(biddingDocumentDto);
  }

  @PreAuthorize("hasRole('MERCHANT')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editBiddingDocument(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    BiddingDocument biddingDocument = biddingDocumentService.editBiddingDocument(id, updates);
    BiddingDocumentDto biddingDocumentDto = BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument);
    return ResponseEntity.ok(biddingDocumentDto);
  }

  @PreAuthorize("hasRole('MERCHANT')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteBiddingDocument(@PathVariable Long id) {
    biddingDocumentService.removeBiddingDocument(id);
    return ResponseEntity.ok(new MessageResponse("Bidding document deleted successfully."));
  }
  
}
