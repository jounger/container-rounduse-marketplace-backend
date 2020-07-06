package com.crm.models.mapper;

import java.util.HashSet;
import java.util.Set;

import com.crm.common.Tool;
import com.crm.models.BillOfLading;
import com.crm.models.Container;
import com.crm.models.dto.BillOfLadingDto;
import com.crm.models.dto.ContainerDto;

public class BillOfLadingMapper {

  public static BillOfLadingDto toBillOfLadingDto(BillOfLading billOfLading) {

    BillOfLadingDto billOfLadingDto = new BillOfLadingDto();
    billOfLadingDto.setId(billOfLading.getId());
    billOfLadingDto.setBillOfLadingNumber(billOfLading.getBillOfLadingNumber());
    billOfLadingDto.setFreeTime(Tool.convertLocalDateTimeToString(billOfLading.getFreeTime()));
    String portOfDelivery = billOfLading.getPortOfDelivery().getNameCode();
    billOfLadingDto.setPortOfDelivery(portOfDelivery);

    Set<Container> containers = new HashSet<Container>(billOfLading.getContainers());
    Set<ContainerDto> containerDtos = new HashSet<>();
    if (containers != null) {
      containers.forEach(container -> {
        ContainerDto containerMap = new ContainerDto();
        containerMap = ContainerMapper.toContainerDto(container);
        containerDtos.add(containerMap);
      });
    }
    billOfLadingDto.setContainers(containerDtos);
    return billOfLadingDto;
  }
}
