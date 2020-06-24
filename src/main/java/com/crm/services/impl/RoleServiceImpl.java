package com.crm.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Permission;
import com.crm.models.Role;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RoleRequest;
import com.crm.repository.PermissionRepository;
import com.crm.repository.RoleRepository;
import com.crm.services.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleRepository roleRepository;
  
  @Autowired
  private PermissionRepository permissionRepository;

  @Override
  public void createRole(RoleRequest request) {
    Role role = new Role();
    if (roleRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException("Role already exists.");
    }
    role.setName(request.getName());
    List<String> permissionsString = request.getPermissions();
    permissionsString.forEach(permission -> {
      Permission rolePermission = permissionRepository.findByName(permission)
          .orElseThrow(() -> new NotFoundException("Permission is not found."));
      role.getPermissions().add(rolePermission);
    });
    roleRepository.save(role);
  }

  @Override
  public Page<Role> getRoles(PaginationRequest request) {
    Page<Role> pages = roleRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public void removeRole(Long id) {
    Role role = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role is not found"));
    roleRepository.delete(role);
  }

  @Override
  public void updateRole(RoleRequest request) {
    Role role = roleRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Role is not found"));
    List<String> permissionsString = request.getPermissions();
    permissionsString.forEach(permission -> {
      Permission rolePermission = permissionRepository.findByName(permission)
          .orElseThrow(() -> new NotFoundException("Permission is not found."));
      role.getPermissions().add(rolePermission);
    });
    role.setName(request.getName());
    roleRepository.save(role);
  }

}
