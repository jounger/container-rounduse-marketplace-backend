package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Permission;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PermissionRequest;
import com.crm.repository.PermissionRepository;
import com.crm.services.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {

  @Autowired
  PermissionRepository permissionRepository;

  @Override
  public Permission createPermission(PermissionRequest request) {
    Permission permission = new Permission();
    if (permissionRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException("Permission already exists.");
    } else {
      permission.setName(request.getName());
      permission.setDescription(request.getDescription());
      permissionRepository.save(permission);
    }

    return permission;
  }

  @Override
  public Page<Permission> getPermissions(PaginationRequest request) {
    Page<Permission> pages = permissionRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return pages;
  }

  @Override
  public void removePermission(Long id) {
    Permission permission = permissionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Permission is not found."));
    permissionRepository.delete(permission);
  }

  @Override
  public Permission updatePermission(PermissionRequest request) {
    Permission permission = permissionRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Permission is not found."));
    permission.setName(request.getName());
    permission.setDescription(request.getDescription());
    permissionRepository.save(permission);
    
    return permission;
  }

}
