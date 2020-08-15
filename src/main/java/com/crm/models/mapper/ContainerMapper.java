package com.crm.models.mapper;

import com.crm.models.Container;
import com.crm.models.dto.ContainerDto;

public class ContainerMapper {

  public static ContainerDto toContainerDto(Container container) {
    if (container == null) {
      return null;
    }

    ContainerDto containerDto = new ContainerDto();
    containerDto.setId(container.getId());
    containerDto.setDriver(DriverMapper.toDriverDto(container.getDriver()));
    containerDto.setNumber(container.getNumber());
    containerDto.setStatus(container.getStatus());
    containerDto.setTractor(ContainerTractorMapper.toContainerTractorDto(container.getTractor()));
    containerDto.setTrailer(ContainerSemiTrailerMapper.toContainerSemiTrailerDto(container.getTrailer()));
    return containerDto;
  }

}
