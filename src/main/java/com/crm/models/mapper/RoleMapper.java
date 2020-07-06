package com.crm.models.mapper;

import java.util.ArrayList;
import java.util.List;

import com.crm.models.Permission;
import com.crm.models.Role;
import com.crm.models.dto.RoleDto;

public class RoleMapper {

  public static RoleDto toRoleDto(Role role) {
    
    RoleDto roleDto = new RoleDto();
    roleDto.setId(role.getId());
    roleDto.setName(role.getName());
    List<String> permissionsDto = new ArrayList<>();
    ArrayList<Permission> permissions = new ArrayList<Permission>(role.getPermissions());
    permissions.forEach(permission -> permissionsDto.add(permission.getName()));
    roleDto.setPermissions(permissionsDto);
    return roleDto;    
  }
}
