package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.BillOfLading;
import com.crm.models.dto.BillOfLadingDto;

public class BillOfLadingMapper {

  public static BillOfLadingDto toBillOfLadingDto(BillOfLading billOfLading) {

    BillOfLadingDto billOfLadingDto = new BillOfLadingDto();
    billOfLadingDto.setId(billOfLading.getId());
    billOfLadingDto.setBillOfLadingNumber(billOfLading.getBillOfLadingNumber());
    billOfLadingDto.setFreeTime(Tool.convertLocalDateTimeToString(billOfLading.getFreeTime()));
    String portOfDelivery = billOfLading.getPortOfDelivery().getNameCode();
    billOfLadingDto.setPortOfDelivery(portOfDelivery);
    billOfLadingDto.setUnit(billOfLading.getUnit());
    return billOfLadingDto;
  }
}
