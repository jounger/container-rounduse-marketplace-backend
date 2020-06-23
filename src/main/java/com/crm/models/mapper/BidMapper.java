package com.crm.models.mapper;

public class BidMapper {
  /*
  public static BidDto toBidDto(Bid bid) {
    BidDto bidDto = new BidDto();
    
    String bidder = bid.getBidder().getUsername();
    bidDto.setBidder(bidder);
    
    ContainerDto container = ContainerMapper.toContainerDto(bid.getContainer());
    bidDto.setContainer(container);
    
    Float bidPrice = bid.getBidPrice();
    bidDto.setBidPrice(bidPrice);
    
    Float currentBidPrice = bid.getCurrentBidPrice();
    bidDto.setCurrentBidPrice(currentBidPrice);
    
    String bidDate = Tool.convertLocalDateTimeToString(bid.getBidDate());
    bidDto.setBidDate(bidDate);
    
    String bidValidityPeriod = Tool.convertLocalDateTimeToString(bid.getBidValidityPeriod());
    bidDto.setBidValidityPeriod(bidValidityPeriod);
    
    String status = bid.getStatus().name();
    bidDto.setStatus(status);
    
    return bidDto;
  }
  */
}
