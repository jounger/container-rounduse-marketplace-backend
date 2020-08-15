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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.enums.EnumUserStatus;
import com.crm.models.Supplier;
import com.crm.models.dto.SupplierDto;
import com.crm.models.mapper.SupplierMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.SupplierRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.SupplierService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

  private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

  @Autowired
  private SupplierService supplierService;

  @PreAuthorize("hasRole('MODERATOR')")
  @GetMapping("")
  public ResponseEntity<?> getSuppliers(@Valid PaginationRequest request) {
    logger.info("Page request: {}", request.getPage());

    Page<Supplier> pages = supplierService.getSuppliers(request);
    PaginationResponse<SupplierDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Supplier> suppliers = pages.getContent();
    List<SupplierDto> suppliersDto = new ArrayList<>();
    suppliers.forEach(supplier -> suppliersDto.add(SupplierMapper.toSupplierDto(supplier)));
    response.setContents(suppliersDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MODERATOR')")
  @GetMapping("/role")
  public ResponseEntity<?> getSuppliersByRole(@Valid PaginationRequest request) {
    logger.info("Page request: {}", request.getPage());

    Page<Supplier> pages = supplierService.getSuppliersByRole(request);
    PaginationResponse<SupplierDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Supplier> suppliers = pages.getContent();
    List<SupplierDto> suppliersDto = new ArrayList<>();
    suppliers.forEach(supplier -> suppliersDto.add(SupplierMapper.toSupplierDto(supplier)));
    response.setContents(suppliersDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MODERATOR')")
  @GetMapping("/status")
  public ResponseEntity<?> getSuppliersByStatus(@Valid PaginationRequest request) {
    logger.info("Page request: {}", request.getPage());

    Page<Supplier> pages = supplierService.getSuppliersByStatus(request);
    PaginationResponse<SupplierDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Supplier> suppliers = pages.getContent();
    List<SupplierDto> suppliersDto = new ArrayList<>();
    suppliers.forEach(supplier -> suppliersDto.add(SupplierMapper.toSupplierDto(supplier)));
    response.setContents(suppliersDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER') or hasRole('MERCHANT')")
  @GetMapping("/registration")
  public ResponseEntity<?> getSupplier(@Valid SupplierRequest request) {
    Supplier supplier = supplierService.getSupplier(request.getUsername());
    SupplierDto supplierDto = SupplierMapper.toSupplierDto(supplier);
    return ResponseEntity.ok(supplierDto);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER') or hasRole('MERCHANT') or hasRole('SHIPPINGLINE')")
  @GetMapping("/{username}")
  public ResponseEntity<?> getSupplier(@PathVariable("username") String username) {
    Supplier supplier = supplierService.getSupplier(username);
    SupplierDto supplierDto = SupplierMapper.toSupplierDto(supplier);
    return ResponseEntity.ok(supplierDto);
  }

  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER') or hasRole('MERCHANT')")
  @GetMapping("/filter")
  public ResponseEntity<?> searchSuppliers(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    Page<Supplier> pages = supplierService.searchSuppliers(request, search);
    PaginationResponse<SupplierDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Supplier> suppliers = pages.getContent();
    List<SupplierDto> suppliersDto = new ArrayList<>();
    suppliers.forEach(supplier -> suppliersDto.add(SupplierMapper.toSupplierDto(supplier)));
    response.setContents(suppliersDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MODERATOR')")
  @RequestMapping(value = "/register/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> reviewRegister(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    Supplier supplier = supplierService.editSupplier(updates, id);
    SupplierDto supplierDto = SupplierMapper.toSupplierDto(supplier);

    // Set default response body
    DefaultResponse<SupplierDto> defaultResponse = new DefaultResponse<>();
    String status = supplier.getStatus();
    if (status.equals(EnumUserStatus.ACTIVE.name())) {
      defaultResponse.setMessage(SuccessMessage.ACCEPT_SUPPLIER_SUCCESSFULLY);
    }
    if (status.equals(EnumUserStatus.REJECT.name())) {
      defaultResponse.setMessage(SuccessMessage.REJECT_SUPPLIER_SUCCESSFULLY);
    }
    defaultResponse.setData(supplierDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER') or hasRole('MERCHANT')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editSupplier(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    Supplier supplier = supplierService.editSupplier(updates, id);
    SupplierDto supplierDto = SupplierMapper.toSupplierDto(supplier);

    // Set default response body
    DefaultResponse<SupplierDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_SUPPLIER_SUCCESSFULLY);
    defaultResponse.setData(supplierDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
