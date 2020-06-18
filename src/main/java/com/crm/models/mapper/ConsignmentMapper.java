package com.crm.models.mapper;

import java.util.HashSet;
import java.util.Set;

import com.crm.common.Tool;
import com.crm.models.Consignment;
import com.crm.models.dto.ConsignmentDto;

public class ConsignmentMapper {
  
  
  
  public static ConsignmentDto toConsignmentDto(Consignment consignment) {
    ConsignmentDto dto = new ConsignmentDto();
    dto.setShippingLine(consignment.getShippingLine().getShortName());
    dto.setContainerType(consignment.getContainerType().getName());
    dto.setStatus(consignment.getStatus().name());
//    dto.setMerchantId(consignment.getMerchant().getId());
    Set<String> categories = new HashSet<>();
    consignment.getCategoryList().forEach(item -> categories.add(item.getName()));
    dto.setCategoryList(categories);
    
    String packingTime = Tool.convertLocalDateTimeToString(consignment.getPackingTime());
    dto.setPackingTime(packingTime);
    
//    dto.setPackingStation(consignment.getAddress());
    dto.setBookingNumber(consignment.getBookingNumber());
    
    String laytime = Tool.convertLocalDateTimeToString(consignment.getLayTime());
    dto.setLaytime(laytime);
    
    String cutOfTime = Tool.convertLocalDateTimeToString(consignment.getCutOfTime());
    dto.setCutOfTime(cutOfTime);
    
    dto.setPayload(consignment.getPayload());
    dto.setUnitOfMeasurement(consignment.getUnitOfMeasurement());
    dto.setPortOfLoading(consignment.getPort().getName());
    return dto;
    
  }
}
