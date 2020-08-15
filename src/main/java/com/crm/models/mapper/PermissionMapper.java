package com.crm.models.mapper;

import com.crm.models.Permission;
import com.crm.models.dto.PermissionDto;

public class PermissionMapper {

  public static PermissionDto toPermissionDto(Permission permission) {
    if (permission == null) {
      return null;
    }

    PermissionDto permissionDto = new PermissionDto();
    permissionDto.setId(permission.getId());
    permissionDto.setName(permission.getName());
    permissionDto.setDescription(permission.getDescription());
    return permissionDto;
  }

}
