package com.crm.models.mapper;

import com.crm.models.Permission;
import com.crm.models.dto.PermissionDto;

public class PermissionMapper {
  
  public static PermissionDto toPermissionDto(Permission permission) {
    
    PermissionDto permissionDto = new PermissionDto();
    permissionDto.setName(permission.getName());
    permissionDto.setDesciption(permission.getDescription());
    return permissionDto;
  }
  
}
