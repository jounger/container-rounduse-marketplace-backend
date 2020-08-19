package com.crm.models.mapper;

import com.crm.models.ContractDocument;
import com.crm.models.Supplier;
import com.crm.models.dto.ContractDocumentDto;

public class ContractDocumentMapper {

  public static ContractDocumentDto toContractDocumentDto(ContractDocument contractDocument) {
    if (contractDocument == null) {
      return null;
    }

    ContractDocumentDto contractDocumentDto = new ContractDocumentDto();
    contractDocumentDto.setId(contractDocument.getId());

    Supplier sender = contractDocument.getSender();
    contractDocumentDto.setSender(SupplierMapper.toSupplierDto(sender));
    contractDocumentDto.setDocumentPath(contractDocument.getDocumentPath());
    contractDocumentDto.setStatus(contractDocument.getStatus());

    return contractDocumentDto;
  }
}
