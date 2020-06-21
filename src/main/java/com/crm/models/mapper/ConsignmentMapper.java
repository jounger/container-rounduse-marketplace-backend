package com.crm.models.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.crm.common.Tool;
import com.crm.models.Consignment;
import com.crm.models.dto.ConsignmentDto;

public class ConsignmentMapper {
  
  
  
  public static ConsignmentDto toConsignmentDto(Consignment consignment) {
    ConsignmentDto dto = new ConsignmentDto();
    
    dto.setId(consignment.getId());
    dto.setShippingLine(consignment.getShippingLine().getCompanyCode());
    dto.setContainerType(consignment.getContainerType().getName());
    dto.setStatus(consignment.getStatus().name());
    dto.setMerchantId(consignment.getMerchant().getId());
    
    Set<String> categories = new HashSet<>();
    consignment.getCategories().forEach(item -> categories.add(item.getName()));
    dto.setCategoryList(categories);
    
    String packingTime = Tool.convertLocalDateTimeToString(consignment.getPackingTime());
    dto.setPackingTime(packingTime);
    
    if(consignment.getPackingStation() != null) {
      Map<String, String> packingStation = new HashMap<>();
      packingStation = AddressMapper.toAddressHashMap(consignment.getPackingStation());
      dto.setPackingStation(packingStation);      
    }
    
    dto.setBookingNumber(consignment.getBookingNumber());
    
    String laytime = Tool.convertLocalDateTimeToString(consignment.getLaytime());
    dto.setLaytime(laytime);
    
    String cutOfTime = Tool.convertLocalDateTimeToString(consignment.getCutOfTime());
    dto.setCutOfTime(cutOfTime);
    
    dto.setPayload(consignment.getPayload());
    dto.setUnitOfMeasurement(consignment.getUnitOfMeasurement().name());
    dto.setPortOfLoading(consignment.getPortOfLoading().getFullname());
    return dto;
    
  }
}
