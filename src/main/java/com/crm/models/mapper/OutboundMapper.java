package com.crm.models.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.crm.common.Tool;
import com.crm.models.Outbound;
import com.crm.models.dto.OutboundDto;

public class OutboundMapper {
  
  
  
  public static OutboundDto toOutboundDto(Outbound outbound) {
    OutboundDto dto = new OutboundDto();
    
    dto.setId(outbound.getId());
    dto.setShippingLine(outbound.getShippingLine().getCompanyCode());
    dto.setContainerType(outbound.getContainerType().getName());
    dto.setStatus(outbound.getStatus());
    dto.setMerchantId(outbound.getMerchant().getId());
    
    
    String packingTime = Tool.convertLocalDateTimeToString(outbound.getPackingTime());
    dto.setPackingTime(packingTime);
  
    dto.setPackingStation(outbound.getPackingStation());
    
    dto.setPayload(outbound.getPayload());
    dto.setUnitOfMeasurement(outbound.getUnitOfMeasurement());
    dto.setFcl(true);
    return dto;
    
  }
}
