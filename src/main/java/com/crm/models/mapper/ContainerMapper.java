package com.crm.models.mapper;

import com.crm.models.Container;
import com.crm.models.dto.ContainerDto;

public class ContainerMapper {

  public static ContainerDto toContainerDto(Container container) {

    ContainerDto containerDto = new ContainerDto();
    containerDto.setId(container.getId());
    containerDto.setDriver(container.getDriver().getUsername());
    containerDto.setContainerNumber(container.getContainerNumber());
    containerDto.setStatus(container.getStatus());
    containerDto.setTractor(ContainerTractorMapper.toContainerTractorDto(container.getTractor()));
    containerDto.setTrailer(ContainerSemiTrailerMapper.toContainerSemiTrailerDto(container.getTrailer()));
    return containerDto;
  }

}
