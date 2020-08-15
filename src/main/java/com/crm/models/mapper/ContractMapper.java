package com.crm.models.mapper;

import com.crm.models.Contract;
import com.crm.models.dto.ContractDto;

public class ContractMapper {

  public static ContractDto toContractDto(Contract contract) {
    if (contract == null) {
      return null;
    }
    
    ContractDto contractDto = new ContractDto();
    contractDto.setId(contract.getId());

    contractDto.setPrice(contract.getPrice());

    contractDto.setFinesAgainstContractViolation(contract.getFinesAgainstContractViolations());

    if (contract.getDiscount() != null) {
      contractDto.setDiscountCode(DiscountMapper.toDiscountDto(contract.getDiscount()));
    }

    contractDto.setRequired(contract.getRequired());

    return contractDto;
  }
}
