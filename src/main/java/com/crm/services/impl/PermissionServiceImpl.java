package com.crm.services.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumPermission;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Permission;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PermissionRequest;
import com.crm.repository.PermissionRepository;
import com.crm.services.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService{

	@Autowired
	PermissionRepository permissionRepository;
	
	@Override
	public void savePermission(PermissionRequest request) {
		List<EnumPermission> permissions = Arrays.asList(EnumPermission.values());
	    int count = 0;
	    for(int i = 0; i < permissions.size(); i++) {
	      if(request.getName().equalsIgnoreCase(permissions.get(i).name().split("_")[1])) {
	        Permission permission = new Permission();
	        permission.setName(permissions.get(i));
	        if(permissionRepository.existsByName(permission.getName())) {
	          throw new DuplicateRecordException("Error: Permission has been existed");
	        }
	        count++;
	        permissionRepository.save(permission);
	      }
	    }
	    if(count == 0) {
	      throw new NotFoundException("Error: Permission is not in bound");
	    }
		
	}

	@Override
	public Page<Permission> getPermission(PaginationRequest request) {
		
		Page<Permission> pages = permissionRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
	    return pages;
	}

}
