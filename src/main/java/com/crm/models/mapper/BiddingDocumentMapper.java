package com.crm.models.mapper;

import java.util.ArrayList;
import java.util.List;

import com.crm.common.Tool;
import com.crm.models.BiddingDocument;
import com.crm.models.dto.BiddingDocumentDto;
import com.crm.models.dto.ConsignmentDto;

public class BiddingDocumentMapper {

  public static BiddingDocumentDto toBiddingDocumentDto(BiddingDocument biddingDocument) {
    BiddingDocumentDto biddingDocumentDto = new BiddingDocumentDto();
    
    String merchantUsername = biddingDocument.getMerchant().getUsername();
    biddingDocumentDto.setMerchant(merchantUsername);
    
    ConsignmentDto consignment = ConsignmentMapper.toConsignmentDto(biddingDocument.getConsignment());
    biddingDocumentDto.setConsignment(consignment);
    
    List<String> bids = new ArrayList<>();
    biddingDocument.getBids().forEach(bid -> {
      bids.add(String.valueOf(bid.getId()));
    });
    String currencyOfPayment = biddingDocument.getCurrencyOfPayment().name();
    biddingDocumentDto.setCurrencyOfPayment(currencyOfPayment);
    
    String bidOpening = Tool.convertLocalDateTimeToString(biddingDocument.getBidOpening());
    biddingDocumentDto.setBidOpening(bidOpening);
    
    String bidClosing = Tool.convertLocalDateTimeToString(biddingDocument.getBidClosing());
    biddingDocumentDto.setBidClosing(bidClosing);
    
    float bidPackagePrice = biddingDocument.getBidPackagePrice();
    biddingDocumentDto.setBidPackagePrice(bidPackagePrice);
    
    float bidFloorPrice = biddingDocument.getBidFloorPrice();
    biddingDocumentDto.setBidFloorPrice(bidFloorPrice);
    
    float bidStep = biddingDocument.getBidStep();
    biddingDocumentDto.setBidStep(bidStep);
    
    String bidDiscountCode = biddingDocument.getBidDiscountCode().getCode();
    biddingDocumentDto.setBidDiscountCode(bidDiscountCode);
    
    float priceLeadership = biddingDocument.getPriceLeadership();
    biddingDocumentDto.setPriceLeadership(priceLeadership);
    
    return biddingDocumentDto;
  }
}
