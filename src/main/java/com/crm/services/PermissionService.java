package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Permission;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PermissionRequest;

public interface PermissionService {

  Permission createPermission(PermissionRequest request);

  Page<Permission> getPermissions(PaginationRequest request);

  Permission updatePermission(PermissionRequest request);
  
  void removePermission(Long id);

  
}
