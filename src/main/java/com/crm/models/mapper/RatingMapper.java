package com.crm.models.mapper;

import com.crm.models.Contract;
import com.crm.models.Rating;
import com.crm.models.Supplier;
import com.crm.models.dto.RatingDto;

public class RatingMapper {

  public static RatingDto toRatingDto(Rating rating) {
    RatingDto ratingDto = new RatingDto();
    ratingDto.setId(rating.getId());

    Supplier sender = rating.getSender();
    ratingDto.setSender(sender.getUsername());

    Supplier receiver = rating.getReceiver();
    ratingDto.setReceiver(receiver.getUsername());

    Contract contract = rating.getContract();
    ratingDto.setContract(ContractMapper.toContractDto(contract));

    ratingDto.setRatingValue(rating.getRatingValue());
    return ratingDto;
  }
}
