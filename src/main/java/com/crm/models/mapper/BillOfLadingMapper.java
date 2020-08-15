package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.BillOfLading;
import com.crm.models.dto.BillOfLadingDto;

public class BillOfLadingMapper {

  public static BillOfLadingDto toBillOfLadingDto(BillOfLading billOfLading) {
    if (billOfLading == null) {
      return null;
    }

    BillOfLadingDto billOfLadingDto = new BillOfLadingDto();
    billOfLadingDto.setId(billOfLading.getId());
    billOfLadingDto.setNumber(billOfLading.getNumber());
    billOfLadingDto.setFreeTime(Tool.convertLocalDateTimeToString(billOfLading.getFreeTime()));
    billOfLadingDto.setPortOfDelivery(PortMapper.toPortDto(billOfLading.getPortOfDelivery()));
    billOfLadingDto.setUnit(billOfLading.getUnit());
    return billOfLadingDto;
  }
}
