package com.crm.models.mapper;

import com.crm.models.Port;
import com.crm.models.dto.PortDto;

public class PortMapper {
  
  public static PortDto toPortDto(Port port) {
    PortDto portDto = new PortDto();
    portDto.setId(port.getId());
    portDto.setFullname(port.getFullname());
    portDto.setNameCode(port.getNameCode());
    portDto.setAddress(port.getAddress());
    
    return portDto;    
  }
}
