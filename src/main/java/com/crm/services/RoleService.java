package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Role;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RoleRequest;

public interface RoleService {
  
  Role createRole(RoleRequest request);
  
  Page<Role> getRoles(PaginationRequest request);
  
  Role updateRole(RoleRequest request);
  
  void removeRole(Long id);
  
}
