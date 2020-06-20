package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Permission;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PermissionRequest;

public interface PermissionService {

	void savePermission(PermissionRequest request);

	Page<Permission> getPermission(PaginationRequest request);
	
	void deletePermission(PermissionRequest request);
	
	void updatePermission(PermissionRequest request);
}
