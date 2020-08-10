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

import com.crm.models.Contract;
import com.crm.models.dto.ContractDto;
import com.crm.models.mapper.ContractMapper;
import com.crm.payload.request.ContractRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ContractService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/contract")
public class ContractController {

  @Autowired
  private ContractService contractService;

  @Transactional
  @PostMapping("/combined/{id}")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  public ResponseEntity<?> createContract(@PathVariable("id") Long id, @Valid @RequestBody ContractRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Contract contract = contractService.createContract(id, username, request);
    ContractDto contractDto = ContractMapper.toContractDto(contract);
    return ResponseEntity.ok(contractDto);
  }
  
  @GetMapping("/combined/{id}")
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  public ResponseEntity<?> getContractByCombined(@PathVariable("id") Long id, @Valid @RequestBody ContractRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Contract contract = contractService.getContractByCombined(id, username);
    ContractDto contractDto = ContractMapper.toContractDto(contract);
    return ResponseEntity.ok(contractDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/user")
  public ResponseEntity<?> getContractsByUser(@Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Contract> pages = contractService.getContractsByUser(username, request);

    PaginationResponse<ContractDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Contract> contracts = pages.getContent();
    List<ContractDto> contractsDto = new ArrayList<>();
    contracts.forEach(contract -> contractsDto.add(ContractMapper.toContractDto(contract)));
    response.setContents(contractsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/filter")
  public ResponseEntity<?> searchContracts(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    Page<Contract> pages = contractService.searchContracts(request, search);
    PaginationResponse<ContractDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Contract> contracts = pages.getContent();
    List<ContractDto> contractsDto = new ArrayList<>();
    contracts.forEach(contract -> contractsDto.add(ContractMapper.toContractDto(contract)));
    response.setContents(contractsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editContract(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Contract contract = contractService.editContract(id, username, updates);
    ContractDto contractDto = ContractMapper.toContractDto(contract);
    return ResponseEntity.ok(contractDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteContract(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    contractService.removeContract(id, username);
    return ResponseEntity.ok(new MessageResponse("Contract deleted successfully."));
  }
}
