package com.crm.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Role;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RoleRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.RoleService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/admin")
public class RoleController {
  
  @Autowired
  private RoleService roleService;
  
  @GetMapping("/role")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getRoles(@Valid @RequestBody PaginationRequest request) {
    Page<Role> pages = roleService.getRoles(request);
    PaginationResponse<Role> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());
    response.setContents(pages.getContent());
    return ResponseEntity.ok(response);
  }
  
  @PostMapping("/role")
  public ResponseEntity<?> createRole(@Valid @RequestBody RoleRequest role) {
    roleService.saveRole(role);
    return ResponseEntity.badRequest().body(new MessageResponse("Role has been created successfully"));
  }
  
}
