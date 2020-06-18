package com.crm.models.mapper;

import com.crm.models.Role;
import com.crm.models.dto.RoleDto;

public class RoleMapper {

  public static RoleDto toRoleDto(Role role) {
    
    RoleDto roleDto = new RoleDto();
    roleDto.setName(role.getName());
    return roleDto;
    
  }
}
