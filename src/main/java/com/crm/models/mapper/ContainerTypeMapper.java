package com.crm.models.mapper;

import com.crm.models.ContainerType;
import com.crm.models.dto.ContainerTypeDto;

public class ContainerTypeMapper {
  public static ContainerTypeDto toContainerTypeDto(ContainerType containerType) {
    ContainerTypeDto containerTypeDto = new ContainerTypeDto();
    containerTypeDto.setId(containerType.getId());
    containerTypeDto.setName(containerType.getName());
    containerTypeDto.setDescription(containerType.getDescription());
    containerTypeDto.setTareWeight(containerType.getTareWeight());
    containerTypeDto.setPayloadCapacity(containerType.getPayloadCapacity());
    containerTypeDto.setCubicCapacity(containerType.getCubicCapacity());
    containerTypeDto.setInternalLength(containerType.getInternalLength());
    containerTypeDto.setInternalWidth(containerType.getInternalWidth());
    containerTypeDto.setInternalHeight(containerType.getInternalHeight());
    containerTypeDto.setDoorOpeningWidth(containerType.getDoorOpeningWidth());
    containerTypeDto.setDoorOpeningHeight(containerType.getDoorOpeningHeight());
    containerTypeDto.setUnitOfMeasurement(containerType.getUnitOfMeasurement());
    return containerTypeDto;
  }
}
