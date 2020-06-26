package com.crm.models.mapper;

import java.util.ArrayList;
import java.util.List;

import com.crm.common.Tool;
import com.crm.models.BiddingDocument;
import com.crm.models.dto.BiddingDocumentDto;

public class BiddingDocumentMapper {

  public static BiddingDocumentDto toBiddingDocumentDto(BiddingDocument biddingDocument) {
    BiddingDocumentDto biddingDocumentDto = new BiddingDocumentDto();

    String merchantUsername = biddingDocument.getOfferee().getUsername();
    biddingDocumentDto.setMerchant(merchantUsername);
    /*
     * OutboundDto consignment =
     * OutboundMapper.toConsignmentDto(biddingDocument.getOutbound());
     * biddingDocumentDto.setConsignment(consignment);
     */
    List<String> bids = new ArrayList<>();
    biddingDocument.getBids().forEach(bid -> {
      bids.add(String.valueOf(bid.getId()));
    });
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

    String bidDiscountCode = biddingDocument.getBidDiscountCode().getCode();
    biddingDocumentDto.setBidDiscountCode(bidDiscountCode);

    Double priceLeadership = biddingDocument.getPriceLeadership();
    biddingDocumentDto.setPriceLeadership(priceLeadership);

    return biddingDocumentDto;
  }
}
