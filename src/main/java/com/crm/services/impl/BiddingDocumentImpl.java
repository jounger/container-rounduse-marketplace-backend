package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.crm.common.Tool;
import com.crm.enums.EnumCurrency;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Consignment;
import com.crm.models.Merchant;
import com.crm.models.NotificationOfAward;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.ConsignmentRepository;
import com.crm.repository.DiscountRepository;
import com.crm.repository.MerchantRepository;
import com.crm.services.BiddingDocumentService;

public class BiddingDocumentImpl implements BiddingDocumentService {

  @Autowired
  private BiddingDocumentRepository biddingDocumentRepository;

  @Autowired
  private MerchantRepository merchantRepository;

  @Autowired
  private ConsignmentRepository consignmentRepository;

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private DiscountRepository discountRepository;

  @Override
  public void saveBiddingDocument(BiddingDocumentRequest request) {
    BiddingDocument biddingDocument = new BiddingDocument();

    Merchant merchant = new Merchant();
    merchant = merchantRepository.findById(request.getMerchantId())
        .orElseThrow(() -> new NotFoundException("Merchant is not found"));
    biddingDocument.setMerchant(merchant);

    Consignment consignment = new Consignment();
    consignment = consignmentRepository.findById(request.getConsignmentId())
        .orElseThrow(() -> new NotFoundException("Consignment is not found."));
    biddingDocument.setConsignment(consignment);
    LocalDateTime bidOpening = Tool.convertToLocalDateTime(request.getBidOpening());
    biddingDocument.setBidOpening(bidOpening);
    LocalDateTime bidClosing = Tool.convertToLocalDateTime(request.getBidClosing());
    biddingDocument.setBidClosing(bidClosing);
    biddingDocument.setCurrencyOfPayment(EnumCurrency.findByName(request.getCurrencyOfPayment()));

    try {
      biddingDocument.setBidPackagePrice(Float.parseFloat(request.getBidPackagePrice()));
      biddingDocument.setBidFloorPrice(Float.parseFloat(request.getBidFloorPrice()));
      biddingDocument.setBidStep(Float.parseFloat(request.getBidStep()));
      biddingDocument.setPriceLeadership(Float.parseFloat(request.getPriceLeaderShip()));
    } catch (Exception e) {
      throw new InternalException("Parameter must be float");
    }

    biddingDocument.setBidDiscountCode(discountRepository.findByCode(request.getBidDiscountCode())
        .orElseThrow(() -> new NotFoundException("Discount is not found.")));

  }

  @Override
  public BiddingDocument getBiddingDocument(Long id) {
    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument = biddingDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Document bidding is not found."));
    return biddingDocument;
  }

  @Override
  public Page<BiddingDocument> getBiddingDocuments(PaginationRequest request) {
    Page<BiddingDocument> biddingDocuments = biddingDocumentRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return biddingDocuments;
  }

  @Override
  public Page<BiddingDocument> getBiddingDocumentsByMerchant(Long id, PaginationRequest request) {
    Page<BiddingDocument> biddingDocuments = null;
    if (merchantRepository.existsById(id)) {
      biddingDocuments = biddingDocumentRepository.findBiddingDocumentByMerchant(id, PageRequest.of(request.getPage(), request.getLimit()));
    } else {
      throw new NotFoundException("Merchant is not found.");
    }
    return biddingDocuments;
  }

  @Override
  public void deleteBiddingDocument(Long id) {
    if (biddingDocumentRepository.existsById(id)) {
      biddingDocumentRepository.deleteById(id);
    } else {
      throw new NotFoundException("Bidding document is not found");
    }
  }

  @Override
  public BiddingDocument updateBiddingDocument(BiddingDocumentRequest request) {

    BiddingDocument biddingDocument = biddingDocumentRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));
    
    String bidOpening = request.getBidOpening();
    if (bidOpening != null) {
      LocalDateTime bidOpeningTime = Tool.convertToLocalDateTime(bidOpening);
      biddingDocument.setBidOpening(bidOpeningTime);
    }

    String bidClosing = request.getBidClosing();
    if (bidClosing != null) {
      LocalDateTime bidClosingTime = Tool.convertToLocalDateTime(bidClosing);
      biddingDocument.setBidClosing(bidClosingTime);
    }
    
    String currency = request.getCurrencyOfPayment();
    if (currency != null) {
      EnumCurrency currencyOfPayment = EnumCurrency.findByName(currency);
      if (currencyOfPayment == null) {
        currencyOfPayment = EnumCurrency.VND;
      }
      biddingDocument.setCurrencyOfPayment(currencyOfPayment);
    } else {
      biddingDocument.setCurrencyOfPayment(EnumCurrency.VND);
    }
    
    try {
      String packagePriceString = request.getBidPackagePrice();
      if (packagePriceString != null) {
        Float bidPackagePrice = Float.parseFloat(packagePriceString);
        biddingDocument.setBidPackagePrice(bidPackagePrice);
      }

      String floorPriceString = request.getBidFloorPrice();
      if (floorPriceString != null) {
        Float bidFloorPrice = Float.parseFloat(floorPriceString);
        biddingDocument.setBidFloorPrice(bidFloorPrice);
      }

      String stepString = request.getBidStep();
      if (stepString != null) {
        Float bidStep = Float.parseFloat(stepString);
        biddingDocument.setBidStep(bidStep);
      }

      String priceLeadershipString = request.getPriceLeaderShip();
      if (priceLeadershipString != null) {
        Float priceLeadership = Float.parseFloat(priceLeadershipString);
        biddingDocument.setPriceLeadership(priceLeadership);
      }
    } catch (Exception e) {
      throw new InternalException("Parameters must be float.");
    }
    biddingDocumentRepository.save(biddingDocument);
    return biddingDocument;
  }

  @Override
  public BiddingDocument editBiddingDocument(Long id, Map<String, Object> updates) {
    BiddingDocument biddingDocument = biddingDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));

    String bidOpening = (String) updates.get("bid_opening");
    if (bidOpening != null) {
      LocalDateTime bidOpeningTime = Tool.convertToLocalDateTime(bidOpening);
      biddingDocument.setBidOpening(bidOpeningTime);
    }

    String bidClosing = (String) updates.get("bid_closing");
    if (bidClosing != null) {
      LocalDateTime bidClosingTime = Tool.convertToLocalDateTime(bidClosing);
      biddingDocument.setBidClosing(bidClosingTime);
    }

    String currency = (String) updates.get("current_of_payment");
    if (currency != null) {
      EnumCurrency currencyOfPayment = EnumCurrency.findByName(currency);
      if (currencyOfPayment == null) {
        currencyOfPayment = EnumCurrency.VND;
      }
      biddingDocument.setCurrencyOfPayment(currencyOfPayment);
    } else {
      biddingDocument.setCurrencyOfPayment(EnumCurrency.VND);
    }

    try {
      String packagePriceString = (String) updates.get("bid_package_price");
      if (packagePriceString != null) {
        Float bidPackagePrice = Float.parseFloat(packagePriceString);
        biddingDocument.setBidPackagePrice(bidPackagePrice);
      }

      String floorPriceString = (String) updates.get("bid_floor_price");
      if (floorPriceString != null) {
        Float bidFloorPrice = Float.parseFloat(floorPriceString);
        biddingDocument.setBidFloorPrice(bidFloorPrice);
      }

      String stepString = (String) updates.get("bid_step");
      if (stepString != null) {
        Float bidStep = Float.parseFloat(stepString);
        biddingDocument.setBidStep(bidStep);
      }

      String priceLeadershipString = (String) updates.get("price_leadership");
      if (priceLeadershipString != null) {
        Float priceLeadership = Float.parseFloat(priceLeadershipString);
        biddingDocument.setPriceLeadership(priceLeadership);
      }
    } catch (Exception e) {
      throw new InternalException("Parameters must be float.");
    }
    
    NotificationOfAward notificationOfAward = new NotificationOfAward();
    
    Bid successfulBid = new Bid();
    String successfulBidId = (String) updates.get("successful_bid");
    String dateOfDecisionString = (String) updates.get("date_of_decision");
    if(successfulBidId != null) {
      try {
        Long bidId = Long.parseLong(successfulBidId);
        successfulBid = bidRepository.findById(bidId)
            .orElseThrow(() -> new NotFoundException("Bid is not found."));
      } catch (Exception e) {
        throw new InternalException("Parameter must be float.");
      }
      notificationOfAward.setSuccessfulBid(successfulBid);
      if(dateOfDecisionString != null) {
        LocalDateTime dateOfDecision = Tool.convertToLocalDateTime(dateOfDecisionString);
        notificationOfAward.setDateOfDecision(dateOfDecision);
      }
      biddingDocument.setNotificationOfAward(notificationOfAward);
    }
    
    biddingDocumentRepository.save(biddingDocument);
    return biddingDocument;
  }

}
