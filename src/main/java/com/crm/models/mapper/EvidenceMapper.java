package com.crm.models.mapper;

import com.crm.models.Evidence;
import com.crm.models.Supplier;
import com.crm.models.dto.EvidenceDto;

public class EvidenceMapper {

  public static EvidenceDto toEvidenceDto(Evidence evidence) {
    EvidenceDto evidenceDto = new EvidenceDto();
    evidenceDto.setId(evidence.getId());
    
    Supplier sender = evidence.getSender();
    evidenceDto.setSender(sender.getUsername());
    
    if(evidence.getEvidence() != null) {
      evidenceDto.setEvidence(evidence.getEvidence());
    }
    
    if (evidence.getIsValid() != null) {
      evidenceDto.setIsValid(evidence.getIsValid());
    }

    return evidenceDto;
  }
}