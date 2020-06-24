package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Permission;
import com.crm.models.dto.PermissionDto;
import com.crm.models.mapper.PermissionMapper;
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
  public ResponseEntity<?> getPermissions(@Valid PaginationRequest request) {
    Page<Permission> pages = permissionService.getPermission(request);
    PaginationResponse<PermissionDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());
    
    List<Permission> permissions = pages.getContent();
    List<PermissionDto> permissionsDto = new ArrayList<>();
    permissions.forEach(permission -> permissionsDto.add(PermissionMapper.toPermissionDto(permission)));
    response.setContents(permissionsDto);
    
    return ResponseEntity.ok(response);
  }
  
  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updatePermission(@Valid @RequestBody PermissionRequest request) {
    permissionService.updatePermission(request);
    return ResponseEntity.ok(new MessageResponse("Role has been updated successfully"));
  }
  
  @Transactional
  @DeleteMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deletePermission(@Valid @RequestBody PermissionRequest request) {
    permissionService.deletePermission(request);
    return ResponseEntity.ok(new MessageResponse("Role has been deleted successfully"));
  }
  
  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createPermission(@Valid @RequestBody PermissionRequest request) {
    permissionService.createPermission(request);
    return ResponseEntity.ok(new MessageResponse("Role has been created successfully"));
  }
}
