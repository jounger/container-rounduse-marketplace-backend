package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Role;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.RoleRequest;

public interface RoleService {
  
  void saveRole(RoleRequest request);
  
  Page<Role> getRoles(PaginationRequest request);
  
  void deleteRole(long id);
  
  void updateRole(long id, RoleRequest request);
  
}
