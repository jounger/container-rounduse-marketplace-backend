package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Bid;
import com.crm.models.dto.BidDto;
import com.crm.models.dto.ContainerDto;

public class BidMapper {
  
  public static BidDto toBidDto(Bid bid) {
    BidDto bidDto = new BidDto();
    
    String bidder = bid.getBidder().getUsername();
    bidDto.setBidder(bidder);
    
    bid.getContainers().forEach(container -> {
      ContainerDto containerDto = ContainerMapper.toContainerDto(container);
      bidDto.getContainers().add(containerDto);
    });
    
    Double bidPrice = bid.getBidPrice();
    bidDto.setBidPrice(bidPrice);
    
    String bidDate = Tool.convertLocalDateTimeToString(bid.getBidDate());
    bidDto.setBidDate(bidDate);
    
    String bidValidityPeriod = Tool.convertLocalDateTimeToString(bid.getBidValidityPeriod());
    bidDto.setBidValidityPeriod(bidValidityPeriod);  
    
    if(bid.getDateOfDecision() != null) {
      String dateOfDecision = Tool.convertLocalDateTimeToString(bid.getDateOfDecision());
      bidDto.setDateOfDecision(dateOfDecision);
    }
    
    String status = bid.getStatus();
    bidDto.setStatus(status);
    
    return bidDto;
  }
  
}
