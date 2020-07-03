package com.crm.models.mapper;

import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.dto.BiddingDocumentDto;
import com.crm.models.dto.CombinedDto;

public class CombinedMapper {
  
  public static CombinedDto toCombinedDto(Combined combined) {
    CombinedDto combinedDto = new CombinedDto();
    
    combinedDto.setId(combined.getId());
    
    BiddingDocument biddingDocument = combined.getBiddingDocument();
    BiddingDocumentDto biddingDocumentDto = BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument);
    combinedDto.setBiddingDocumentDto(biddingDocumentDto);
    
    combinedDto.setStatus(combined.getStatus());
    
    return combinedDto;
  }
}
