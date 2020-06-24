package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Role;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RoleRequest;

public interface RoleService {
  
  void createRole(RoleRequest request);
  
  Page<Role> getRoles(PaginationRequest request);
  
  void deleteRole(RoleRequest request);
  
  void updateRole(RoleRequest request);
  
}
