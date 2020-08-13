package com.crm.models.mapper;

import java.util.HashSet;
import java.util.Set;

import com.crm.models.Operator;
import com.crm.models.dto.OperatorDto;

public class OperatorMapper {

  public static OperatorDto toOperatorDto(Operator operator) {
    OperatorDto operatorDto = new OperatorDto();
    operatorDto.setId(operator.getId());
    operatorDto.setUsername(operator.getUsername());
    operatorDto.setEmail(operator.getEmail());
    operatorDto.setPhone(operator.getPhone());
    operatorDto.setStatus(operator.getStatus());
    operatorDto.setProfileImagePath(operator.getProfileImagePath());

    Set<String> roles = new HashSet<>();
    operator.getRoles().forEach(role -> roles.add(role.getName()));
    operatorDto.setRoles(roles);
    operatorDto.setAddress(operator.getAddress());
    operatorDto.setFullname(operator.getFullname());
    operatorDto.setIsRoot(operator.getIsRoot());

    return operatorDto;
  }
}
