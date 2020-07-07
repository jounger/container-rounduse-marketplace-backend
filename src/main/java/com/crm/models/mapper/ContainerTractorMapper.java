package com.crm.models.mapper;

import com.crm.models.ContainerTractor;
import com.crm.models.dto.ContainerTractorDto;

public class ContainerTractorMapper {
  public static ContainerTractorDto toContainerTractorDto(ContainerTractor containerTractor) {
    ContainerTractorDto containerTractorDto = new ContainerTractorDto();
    containerTractorDto.setId(containerTractor.getId());
    containerTractorDto.setLicensePlate(containerTractor.getLicensePlate());
    return containerTractorDto;
  }
}
