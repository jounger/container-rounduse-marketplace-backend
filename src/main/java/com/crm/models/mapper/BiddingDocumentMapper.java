package com.crm.models.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.crm.common.Tool;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.dto.BidDto;
import com.crm.models.dto.BiddingDocumentDto;
import com.crm.models.dto.OutboundDto;

public class BiddingDocumentMapper {

  public static BiddingDocumentDto toBiddingDocumentDto(BiddingDocument biddingDocument) {
    BiddingDocumentDto biddingDocumentDto = new BiddingDocumentDto();

    biddingDocumentDto.setId(biddingDocument.getId());
    
    String merchantUsername = biddingDocument.getOfferee().getUsername();
    biddingDocumentDto.setMerchant(merchantUsername);

    OutboundDto outboundDto = OutboundMapper.toOutboundDto(biddingDocument.getOutbound());
    biddingDocumentDto.setOutbound(outboundDto);
    
    biddingDocumentDto.setIsMultipleAward(biddingDocument.getIsMultipleAward());

    List<BidDto> bidsDto = new ArrayList<>();
    List<Bid> bids = biddingDocument.getBids();
    bids.forEach(bid -> {
      BidDto bidDto = BidMapper.toBidDto(bid);
      bidsDto.add(bidDto);
    });
    biddingDocumentDto.setBids(bidsDto);
    
    String currencyOfPayment = biddingDocument.getCurrencyOfPayment();
    biddingDocumentDto.setCurrencyOfPayment(currencyOfPayment);

    String bidOpening = Tool.convertLocalDateTimeToString(biddingDocument.getBidOpening());
    biddingDocumentDto.setBidOpening(bidOpening);

    String bidClosing = Tool.convertLocalDateTimeToString(biddingDocument.getBidClosing());
    biddingDocumentDto.setBidClosing(bidClosing);

    Double bidPackagePrice = biddingDocument.getBidPackagePrice();
    biddingDocumentDto.setBidPackagePrice(bidPackagePrice);

    Double bidFloorPrice = biddingDocument.getBidFloorPrice();
    biddingDocumentDto.setBidFloorPrice(bidFloorPrice);

    if (biddingDocument.getBidDiscountCode() != null) {
      String bidDiscountCode = biddingDocument.getBidDiscountCode().getCode();
      biddingDocumentDto.setBidDiscountCode(bidDiscountCode);
    }

    Double priceLeadership = biddingDocument.getPriceLeadership();
    biddingDocumentDto.setPriceLeadership(priceLeadership);

    return biddingDocumentDto;
  }
  
  public static BiddingDocumentDto toBiddingDocumentDtoForForwarder(BiddingDocument biddingDocument, String username) {
    BiddingDocumentDto biddingDocumentDto = new BiddingDocumentDto();

    biddingDocumentDto.setId(biddingDocument.getId());
    
    String merchantUsername = biddingDocument.getOfferee().getUsername();
    biddingDocumentDto.setMerchant(merchantUsername);

    OutboundDto outboundDto = OutboundMapper.toOutboundDto(biddingDocument.getOutbound());
    biddingDocumentDto.setOutbound(outboundDto);
    
    biddingDocumentDto.setIsMultipleAward(biddingDocument.getIsMultipleAward());

    Bid result = biddingDocument.getBids().stream().filter(bid -> bid.getBidder().getUsername().equalsIgnoreCase(username)).findAny()
        .orElseThrow(() -> new NotFoundException("Bidder is not found."));
    BidDto bidDto = BidMapper.toBidDto(result);
    biddingDocumentDto.setBids(Arrays.asList(bidDto));;
    
    String currencyOfPayment = biddingDocument.getCurrencyOfPayment();
    biddingDocumentDto.setCurrencyOfPayment(currencyOfPayment);

    String bidOpening = Tool.convertLocalDateTimeToString(biddingDocument.getBidOpening());
    biddingDocumentDto.setBidOpening(bidOpening);

    String bidClosing = Tool.convertLocalDateTimeToString(biddingDocument.getBidClosing());
    biddingDocumentDto.setBidClosing(bidClosing);

    Double bidPackagePrice = biddingDocument.getBidPackagePrice();
    biddingDocumentDto.setBidPackagePrice(bidPackagePrice);

    Double bidFloorPrice = biddingDocument.getBidFloorPrice();
    biddingDocumentDto.setBidFloorPrice(bidFloorPrice);

    if (biddingDocument.getBidDiscountCode() != null) {
      String bidDiscountCode = biddingDocument.getBidDiscountCode().getCode();
      biddingDocumentDto.setBidDiscountCode(bidDiscountCode);
    }

    Double priceLeadership = biddingDocument.getPriceLeadership();
    biddingDocumentDto.setPriceLeadership(priceLeadership);

    return biddingDocumentDto;
  }
}
