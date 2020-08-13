package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

import com.crm.common.SuccessMessage;
import com.crm.models.Role;
import com.crm.models.dto.RoleDto;
import com.crm.models.mapper.RoleMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RoleRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.RoleService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/role")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

  @Autowired
  private RoleService roleService;

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createRole(@Valid @RequestBody RoleRequest request) {
    Role role = roleService.createRole(request);
    RoleDto roleDto = RoleMapper.toRoleDto(role);

    // Set default response body
    DefaultResponse<RoleDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_ROLE_SUCCESSFULLY);
    defaultResponse.setData(roleDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @GetMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getRoles(@Valid PaginationRequest request) {
    Page<Role> pages = roleService.getRoles(request);
    PaginationResponse<RoleDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Role> roles = pages.getContent();
    List<RoleDto> rolesDto = new ArrayList<>();
    roles.forEach(role -> rolesDto.add(RoleMapper.toRoleDto(role)));
    response.setContents(rolesDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateRole(@Valid @RequestBody RoleRequest request) {
    Role role = roleService.updateRole(request);
    RoleDto roleDto = RoleMapper.toRoleDto(role);

    // Set default response body
    DefaultResponse<RoleDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_ROLE_SUCCESSFULLY);
    defaultResponse.setData(roleDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> removeRole(@PathVariable Long id) {
    roleService.removeRole(id);

    // Set default response body
    DefaultResponse<RoleDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_ROLE_SUCCESSFULLY);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

}
