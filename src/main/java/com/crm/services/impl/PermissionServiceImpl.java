package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
  public void createPermission(PermissionRequest request) {
    Permission permission = new Permission();
    if(permissionRepository.existsByName(request.getName())) {
      throw new DuplicateRecordException("Permission already exists.");
    }else {
      permission.setName(request.getName());
      permission.setDescription(request.getDescription());
      permissionRepository.save(permission);
    }
  }

  @Override
  public Page<Permission> getPermission(PaginationRequest request) {
    Page<Permission> pages = permissionRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public void deletePermission(PermissionRequest request) {
    Permission permission = permissionRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Permission is not found."));
    permissionRepository.delete(permission);
  }

  @Override
  public void updatePermission(PermissionRequest request) {
    Permission permission = permissionRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Permission is not found."));
    permission.setName(request.getName());
    permission.setDescription(request.getDescription());
  }

}
