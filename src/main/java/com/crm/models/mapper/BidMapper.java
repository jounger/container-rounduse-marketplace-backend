package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Bid;
import com.crm.models.dto.BidDto;

public class BidMapper {

  public static BidDto toBidDto(Bid bid) {
    if (bid == null) {
      return null;
    }

    BidDto bidDto = new BidDto();

    bidDto.setId(bid.getId());

    bidDto.setBidder(ForwarderMapper.toForwarderDto(bid.getBidder()));

    Double bidPrice = bid.getBidPrice();
    bidDto.setBidPrice(bidPrice);

    String bidDate = Tool.convertLocalDateTimeToString(bid.getBidDate());
    bidDto.setBidDate(bidDate);

    String bidValidityPeriod = Tool.convertLocalDateTimeToString(bid.getBidValidityPeriod());
    bidDto.setBidValidityPeriod(bidValidityPeriod);

    if (bid.getDateOfDecision() != null) {
      String dateOfDecision = Tool.convertLocalDateTimeToString(bid.getDateOfDecision());
      bidDto.setDateOfDecision(dateOfDecision);
    }

    String status = bid.getStatus();
    bidDto.setStatus(status);

    return bidDto;
  }

}
