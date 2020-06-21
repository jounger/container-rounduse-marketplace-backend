package com.crm.models.mapper;

import com.crm.models.Icd;
import com.crm.models.dto.IcdDto;

public class IcdMapper {
  public static IcdDto toIcdDto(Icd icd) {
    IcdDto icdDto = new IcdDto();
    icdDto.setId(icd.getId());
    icdDto.setFullname(icd.getFullname());
    icdDto.setNameCode(icd.getNameCode());
    icdDto.setAddress(icd.getAddress());

    return icdDto;    
  }
}
