package com.crm.models.mapper;

import com.crm.models.Contract;
import com.crm.models.dto.ContractDto;

public class ContractMapper {

  public static ContractDto toContractDto(Contract contract) {
    ContractDto contractDto = new ContractDto();
    contractDto.setId(contract.getId());

    contractDto.setPrice(contract.getPrice());

    contractDto.setFinesAgainstContractViolation(contract.getFinesAgainstContractViolations());

    if (contract.getDiscount() != null) {
      String discountCode = contract.getDiscount().getCode();
      contractDto.setDiscountCode(discountCode);
    }

    contractDto.setRequired(contract.getRequired());

    return contractDto;
  }
}
