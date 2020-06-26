package com.crm.models.mapper;

import com.crm.models.Container;
import com.crm.models.dto.ContainerDto;

public class ContainerMapper {

  public static ContainerDto toContainerDto(Container container) {

    ContainerDto containerDto = new ContainerDto();
    containerDto.setId(container.getId());
    containerDto.setDriver(container.getDriver().getUsername());
    containerDto.setTractor(container.getTractor());
    containerDto.setTrailer(container.getTrailer());
    containerDto.setContainerNumber(container.getContainerNumber());
    containerDto.setLicensePlate(container.getLicensePlate());

    return containerDto;
  }

}
