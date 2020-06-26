package com.crm.services.impl;

import org.springframework.stereotype.Service;

import com.crm.services.BiddingDocumentService;

@Service
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
  public void createBiddingDocument(BiddingDocumentRequest request) {
    BiddingDocument biddingDocument = new BiddingDocument();

    Merchant merchant = new Merchant();
    merchant = merchantRepository.findById(request.getMerchantId())
        .orElseThrow(() -> new NotFoundException("Merchant is not found"));
    biddingDocument.setMerchant(merchant);

    Outbound outbound = new Outbound();
    outbound = consignmentRepository.findById(request.getConsignmentId())
        .orElseThrow(() -> new NotFoundException("Consignment is not found."));
    biddingDocument.setConsignment(outbound);

    LocalDateTime bidOpening = Tool.convertToLocalDateTime(request.getBidOpening());
    biddingDocument.setBidOpening(bidOpening);

    LocalDateTime bidClosing = Tool.convertToLocalDateTime(request.getBidClosing());
    biddingDocument.setBidClosing(bidClosing);

    biddingDocument.setCurrencyOfPayment(EnumCurrency.findByName(request.getCurrencyOfPayment()));
    biddingDocument.setBidPackagePrice(request.getBidPackagePrice());
    biddingDocument.setBidFloorPrice(request.getBidFloorPrice());
    biddingDocument.setBidStep(request.getBidStep());
    biddingDocument.setPriceLeadership(request.getBidFloorPrice());

    
    String discountCodeString = request.getBidDiscountCode();
    if(discountCodeString != null) {
      Discount bidDiscountCode = discountRepository.findByCode(discountCodeString)
          .orElseThrow(() -> new NotFoundException("Discount is not found."));
      biddingDocument.setBidDiscountCode(bidDiscountCode);
    }

    biddingDocumentRepository.save(biddingDocument);
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
      biddingDocuments = biddingDocumentRepository.findBiddingDocumentByMerchant(id,
          PageRequest.of(request.getPage(), request.getLimit()));
    } else {
      throw new NotFoundException("Merchant is not found.");
    }
    return biddingDocuments;
  }

  @Override
  public void removeBiddingDocument(Long id) {
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

    LocalDateTime bidOpeningTime = Tool.convertToLocalDateTime(request.getBidOpening());
    biddingDocument.setBidOpening(bidOpeningTime);

    LocalDateTime bidClosingTime = Tool.convertToLocalDateTime(request.getBidClosing());
    biddingDocument.setBidClosing(bidClosingTime);

    EnumCurrency currencyOfPayment = EnumCurrency.findByName(request.getCurrencyOfPayment());
    if (currencyOfPayment == null) {
      currencyOfPayment = EnumCurrency.VND;
    } else {
      biddingDocument.setCurrencyOfPayment(currencyOfPayment);
    }

    biddingDocument.setBidPackagePrice(request.getBidPackagePrice());
    biddingDocument.setBidFloorPrice(request.getBidFloorPrice());
    biddingDocument.setBidStep(request.getBidStep());
    biddingDocument.setPriceLeadership(request.getPriceLeadership());

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
    if (successfulBidId != null) {
      try {
        Long bidId = Long.parseLong(successfulBidId);
        successfulBid = bidRepository.findById(bidId).orElseThrow(() -> new NotFoundException("Bid is not found."));
      } catch (Exception e) {
        throw new InternalException("Parameter must be float.");
      }
      notificationOfAward.setSuccessfulBid(successfulBid);
      if (dateOfDecisionString != null) {
        LocalDateTime dateOfDecision = Tool.convertToLocalDateTime(dateOfDecisionString);
        notificationOfAward.setDateOfDecision(dateOfDecision);
      }
      biddingDocument.setNotificationOfAward(notificationOfAward);
    }

    biddingDocumentRepository.save(biddingDocument);
    return biddingDocument;
  }
  
}
