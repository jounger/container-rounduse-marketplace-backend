package com.crm.models.mapper;

import java.util.ArrayList;
import java.util.List;

import com.crm.common.Tool;
import com.crm.models.BiddingDocument;
import com.crm.models.dto.BidDto;
import com.crm.models.dto.BiddingDocumentDto;
import com.crm.models.dto.OutboundDto;

public class BiddingDocumentMapper {

  public static BiddingDocumentDto toBiddingDocumentDto(BiddingDocument biddingDocument) {
    if (biddingDocument == null) {
      return null;
    }

    BiddingDocumentDto biddingDocumentDto = new BiddingDocumentDto();

    biddingDocumentDto.setId(biddingDocument.getId());

    biddingDocumentDto.setOfferee(MerchantMapper.toMerchantDto(biddingDocument.getOfferee()));

    OutboundDto outboundDto = OutboundMapper.toOutboundDto(biddingDocument.getOutbound());
    biddingDocumentDto.setOutbound(outboundDto);

    biddingDocumentDto.setIsMultipleAward(biddingDocument.getIsMultipleAward());

    List<BidDto> bidsDto = new ArrayList<>();
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

    Double priceLeadership = biddingDocument.getPriceLeadership();
    biddingDocumentDto.setPriceLeadership(priceLeadership);

    String status = biddingDocument.getStatus();
    biddingDocumentDto.setStatus(status);

    return biddingDocumentDto;
  }
}
