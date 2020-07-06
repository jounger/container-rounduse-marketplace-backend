package com.crm.models.mapper;

import com.crm.models.Bid;
import com.crm.models.Combined;
import com.crm.models.dto.BidDto;
import com.crm.models.dto.CombinedDto;

public class CombinedMapper {
  
  public static CombinedDto toCombinedDto(Combined combined) {
    CombinedDto combinedDto = new CombinedDto();
    
    combinedDto.setId(combined.getId());
    
    Bid bid = combined.getBid();
    BidDto bidDto = BidMapper.toBidDto(bid);
    combinedDto.setBidDto(bidDto);
    
    combinedDto.setStatus(combined.getStatus());
    
    return combinedDto;
  }
}
