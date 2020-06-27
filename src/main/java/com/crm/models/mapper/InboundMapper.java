package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Inbound;
import com.crm.models.dto.BillOfLadingDto;
import com.crm.models.dto.InboundDto;

public class InboundMapper {
  public static InboundDto toInboundDto(Inbound inbound) {
    InboundDto inboundDto = new InboundDto();
    inboundDto.setId(inbound.getId());
    String shippingLine = inbound.getShippingLine().getCompanyCode();
    inboundDto.setShippingLine(shippingLine);

    String containerType = inbound.getContainerType().getName();
    inboundDto.setContainerType(containerType);

    if (inbound.getEmptyTime() != null) {
      String emptyTime = Tool.convertLocalDateTimeToString(inbound.getEmptyTime());
      inboundDto.setEmptyTime(emptyTime);
    }

    if (inbound.getPickupTime() != null) {
      String pickUpTime = Tool.convertLocalDateTimeToString(inbound.getPickupTime());
      inboundDto.setPickupTime(pickUpTime);
    }

    BillOfLadingDto billOfLadingDto = BillOfLadingMapping.toBillOfLadingDto(inbound.getBillOfLading());
    inboundDto.setBillOfLading(billOfLadingDto);

    return inboundDto;

  }
}
