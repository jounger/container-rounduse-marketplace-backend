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

import com.crm.models.Permission;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PermissionRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.PermissionService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/permission")
public class PermissionController {

  @Autowired
  private PermissionService permissionService;
  
  @GetMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getPermissions(@Valid @RequestBody PaginationRequest request) {
    Page<Permission> pages = permissionService.getPermission(request);
    PaginationResponse<Permission> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());
    response.setContents(pages.getContent());
    return ResponseEntity.ok(response);
  }
  
  @PostMapping("/{id}")
  public ResponseEntity<?> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionRequest request) {
    permissionService.updatePermission(id, request);
    return ResponseEntity.badRequest().body(new MessageResponse("Role has been created successfully"));
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePermission(@PathVariable Long id) {
    permissionService.deletePermission(id);
    return ResponseEntity.badRequest().body(new MessageResponse("Role has been created successfully"));
  }
  
  @PutMapping("")
  public ResponseEntity<?> createPermission(@Valid @RequestBody PermissionRequest request) {
    permissionService.savePermission(request);
    return ResponseEntity.badRequest().body(new MessageResponse("Role has been created successfully"));
  }
}
