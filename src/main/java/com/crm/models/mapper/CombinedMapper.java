package com.crm.models.mapper;

import com.crm.models.Bid;
import com.crm.models.Combined;
import com.crm.models.Contract;
import com.crm.models.dto.BidDto;
import com.crm.models.dto.CombinedDto;
import com.crm.models.dto.ContractDto;

public class CombinedMapper {

  public static CombinedDto toCombinedDto(Combined combined) {
    CombinedDto combinedDto = new CombinedDto();

    combinedDto.setId(combined.getId());

    Bid bid = combined.getBid();
    BidDto bidDto = BidMapper.toBidDto(bid);
    combinedDto.setBid(bidDto);

    combinedDto.setIsCanceled(combined.getIsCanceled());

    Contract contract = combined.getContract();
    ContractDto contractDto = ContractMapper.toContractDto(contract);
    combinedDto.setContract(contractDto);

    return combinedDto;
  }
}
