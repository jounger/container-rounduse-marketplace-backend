package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Inbound;
import com.crm.models.dto.BillOfLadingDto;
import com.crm.models.dto.InboundDto;

public class InboundMapper {
  public static InboundDto toInboundDto(Inbound inbound) {
    if (inbound == null) {
      return null;
    }

    InboundDto inboundDto = new InboundDto();
    inboundDto.setId(inbound.getId());
    inboundDto.setCode(inbound.getCode());
    inboundDto.setShippingLine(ShippingLineMapper.toShippingLineDto(inbound.getShippingLine()));

    inboundDto.setContainerType(ContainerTypeMapper.toContainerTypeDto(inbound.getContainerType()));

    if (inbound.getEmptyTime() != null) {
      String emptyTime = Tool.convertLocalDateTimeToString(inbound.getEmptyTime());
      inboundDto.setEmptyTime(emptyTime);
    }

    if (inbound.getPickupTime() != null) {
      String pickUpTime = Tool.convertLocalDateTimeToString(inbound.getPickupTime());
      inboundDto.setPickupTime(pickUpTime);
    }

    inboundDto.setReturnStation(inbound.getReturnStation());

    BillOfLadingDto billOfLadingDto = BillOfLadingMapper.toBillOfLadingDto(inbound.getBillOfLading());
    inboundDto.setBillOfLading(billOfLadingDto);

    return inboundDto;

  }
}
