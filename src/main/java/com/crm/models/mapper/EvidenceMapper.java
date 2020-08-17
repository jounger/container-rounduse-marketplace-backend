package com.crm.models.mapper;

import com.crm.models.Evidence;
import com.crm.models.Supplier;
import com.crm.models.dto.EvidenceDto;

public class EvidenceMapper {

  public static EvidenceDto toEvidenceDto(Evidence evidence) {
    if (evidence == null) {
      return null;
    }

    EvidenceDto evidenceDto = new EvidenceDto();
    evidenceDto.setId(evidence.getId());

    Supplier sender = evidence.getSender();
    evidenceDto.setSender(SupplierMapper.toSupplierDto(sender));
    evidenceDto.setDocumentPath(evidence.getDocumentPath());
    evidenceDto.setStatus(evidence.getStatus());

    return evidenceDto;
  }
}
