package com.crm.models.mapper;

import com.crm.models.ContainerSemiTrailer;
import com.crm.models.dto.ContainerSemiTrailerDto;

public class ContainerSemiTrailerMapper {

  public static ContainerSemiTrailerDto toContainerSemiTrailerDto(ContainerSemiTrailer containerSemiTrailer) {
    if (containerSemiTrailer == null) {
      return null;
    }

    ContainerSemiTrailerDto containerSemiTrailerDto = new ContainerSemiTrailerDto();
    containerSemiTrailerDto.setId(containerSemiTrailer.getId());
    containerSemiTrailerDto.setLicensePlate(containerSemiTrailer.getLicensePlate());
    containerSemiTrailerDto.setType(containerSemiTrailer.getType());
    containerSemiTrailerDto.setUnitOfMeasurement(containerSemiTrailer.getUnitOfMeasurement());
    containerSemiTrailerDto.setNumberOfAxles(containerSemiTrailer.getNumberOfAxles());
    return containerSemiTrailerDto;
  }
}
