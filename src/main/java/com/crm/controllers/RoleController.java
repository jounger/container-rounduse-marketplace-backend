package com.crm.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Role;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RoleRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.RoleService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/role")
public class RoleController {
  
  @Autowired
  private RoleService roleService;
  
  @GetMapping("")
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
  
  @PostMapping("/{id}")
  public ResponseEntity<?> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
    roleService.updateRole(id, request);
    return ResponseEntity.badRequest().body(new MessageResponse("Role has been created successfully"));
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id);
    return ResponseEntity.badRequest().body(new MessageResponse("Role has been created successfully"));
  }
  
  @PutMapping("")
  public ResponseEntity<?> createRole(@Valid @RequestBody RoleRequest request) {
    roleService.saveRole(request);
    return ResponseEntity.badRequest().body(new MessageResponse("Role has been created successfully"));
  }
  
}
