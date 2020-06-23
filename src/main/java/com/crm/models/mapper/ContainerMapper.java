package com.crm.models.mapper;

import com.crm.models.Container;
import com.crm.models.dto.ContainerDto;

public class ContainerMapper {
  /*
  public static ContainerDto toContainerDto(Container container) {
    
    ContainerDto containerDto = new ContainerDto();
    containerDto.setId(container.getId());
    containerDto.setShippingLine(container.getShippingLine().getCompanyName());
    containerDto.setContainerType(container.getContainerType().getName());
    containerDto.setStatus(container.getStatus().name());
    containerDto.setForwarderId(container.getForwarder().getId());
    containerDto.setDriverId(container.getDriver().getId());
    containerDto.setContainerTractor(container.getContainerTractor());
    containerDto.setContainerTrailer(container.getContainerTrailer());
    containerDto.setContainerNumber(container.getContainerNumber());
    containerDto.setBlNumber(container.getBlNumber());
    containerDto.setLicensePlate(container.getLicensePlate());

    String emptyTime = Tool.convertLocalDateTimeToString(container.getEmptyTime());
    containerDto.setEmptyTime(emptyTime);

    String pickUpTime = Tool.convertLocalDateTimeToString(container.getPickUpTime());
    containerDto.setPickUpTime(pickUpTime);

    if (container.getReturnStation() != null) {
      Map<String, String> returnStation = new HashMap<>();
      returnStation = AddressMapper.toAddressHashMap(container.getReturnStation());
      containerDto.setReturnStation(returnStation);
    }
    containerDto.setPortOfDelivery(container.getPortOfDelivery().getFullname());
    containerDto.setFreeTime(container.getFreeTime());

    return containerDto;
  }
  */
}
