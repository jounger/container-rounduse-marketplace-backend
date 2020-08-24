package com.crm.models.mapper;

import com.crm.models.Contract;
import com.crm.models.Rating;
import com.crm.models.Supplier;
import com.crm.models.dto.RatingDto;

public class RatingMapper {

  public static RatingDto toRatingDto(Rating rating) {
    if (rating == null) {
      return null;
    }

    RatingDto ratingDto = new RatingDto();
    ratingDto.setId(rating.getId());

    Supplier sender = rating.getSender();
    ratingDto.setSender(SupplierMapper.toSupplierDto(sender));

    Supplier receiver = rating.getReceiver();
    ratingDto.setReceiver(SupplierMapper.toSupplierDto(receiver));

    Contract contract = rating.getContract();
    ratingDto.setContract(ContractMapper.toContractDto(contract));

    ratingDto.setRatingValue(rating.getRatingValue());
    ratingDto.setComment(rating.getComment());

    return ratingDto;
  }
}
