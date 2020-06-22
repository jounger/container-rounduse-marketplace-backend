package com.crm.models.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    operatorDto.setStatus(operator.getStatus().name());
    Set<String> roles = new HashSet<>();
    operator.getRoles().forEach(role -> roles.add(role.getName()));
    operatorDto.setRoles(roles);
    
    if(operator.getAddress() != null) {
      Map<String, String> address = new HashMap<>();
      address = AddressMapper.toAddressHashMap(operator.getAddress());
      operatorDto.setAddress(address);
    }
    
    operatorDto.setFullname(operator.getFullname());
    operatorDto.setRoot(false);
    
    return operatorDto;
  }
}
