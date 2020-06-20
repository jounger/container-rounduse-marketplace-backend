package com.crm.models.mapper;

import java.util.ArrayList;
import java.util.Collection;

import com.crm.models.Icd;
import com.crm.models.dto.IcdDto;

public class IcdMapper {
  public static IcdDto toIcdDto(Icd icd) {
    IcdDto icdDto = new IcdDto();
    icdDto.setFullname(icd.getFullname());
    icdDto.setNameCode(icd.getNameCode());
    icdDto.setAddress(icd.getAddress());
    
    Collection<String> shippingLines = new ArrayList<String>();
    icd.getShippingLines().forEach(item -> shippingLines.add(item.getCompanyName()));
    icdDto.setShippingLines(shippingLines);
    return icdDto;    
  }
}
