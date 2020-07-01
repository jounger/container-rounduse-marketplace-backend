package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumCurrency;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.BiddingDocument;
import com.crm.models.Discount;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.DiscountRepository;
import com.crm.repository.MerchantRepository;
import com.crm.repository.OutboundRepository;
import com.crm.services.BiddingDocumentService;

@Service
public class BiddingDocumentImpl implements BiddingDocumentService {

  @Autowired
  private BiddingDocumentRepository biddingDocumentRepository;

  @Autowired
  private MerchantRepository merchantRepository;

  @Autowired
  private OutboundRepository outboundRepository;

  @Autowired
  private DiscountRepository discountRepository;

  @Override
  public BiddingDocument createBiddingDocument(BiddingDocumentRequest request) {
    BiddingDocument biddingDocument = new BiddingDocument();

    Merchant merchant = new Merchant();
    merchant = merchantRepository.findByUsername(request.getOfferee())
        .orElseThrow(() -> new NotFoundException("Merchant is not found"));
    biddingDocument.setOfferee(merchant);

    Outbound outbound = new Outbound();
    outbound = outboundRepository.findById(request.getOutbound())
        .orElseThrow(() -> new NotFoundException("Outbound is not found."));
    if (outbound.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
        || outbound.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
      throw new DuplicateRecordException("Outbound must be not in any transaction.");
    }
    outbound.setStatus(EnumSupplyStatus.BIDDING.name());
    biddingDocument.setOutbound(outbound);

    biddingDocument.setIsMultipleAward(request.getIsMultipleAward());

    LocalDateTime bidOpening = Tool.convertToLocalDateTime(request.getBidOpening());
    biddingDocument.setBidOpening(bidOpening);

    LocalDateTime bidClosing = Tool.convertToLocalDateTime(request.getBidClosing());
    biddingDocument.setBidClosing(bidClosing);

    EnumCurrency currency = EnumCurrency.findByName(request.getCurrencyOfPayment());
    if (currency != null) {
      biddingDocument.setCurrencyOfPayment(currency.name());
    } else {
      biddingDocument.setCurrencyOfPayment(EnumCurrency.VND.name());
    }

    biddingDocument.setBidPackagePrice(request.getBidPackagePrice());
    biddingDocument.setBidFloorPrice(request.getBidFloorPrice());
    biddingDocument.setPriceLeadership(request.getBidFloorPrice());

    String discountCodeString = request.getBidDiscountCode();
    if (discountCodeString != null) {
      Discount bidDiscountCode = discountRepository.findByCode(discountCodeString)
          .orElseThrow(() -> new NotFoundException("Discount is not found."));
      biddingDocument.setBidDiscountCode(bidDiscountCode);
    }

    biddingDocumentRepository.save(biddingDocument);
    return biddingDocument;
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
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
    return biddingDocuments;
  }

  @Override
  public Page<BiddingDocument> getBiddingDocumentsByMerchant(Long id, PaginationRequest request) {
    Page<BiddingDocument> biddingDocuments = null;
    if (merchantRepository.existsById(id)) {
      biddingDocuments = biddingDocumentRepository.findBiddingDocumentByMerchant(id,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
    } else {
      throw new NotFoundException("Merchant is not found.");
    }
    return biddingDocuments;
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
      biddingDocument.setCurrencyOfPayment(currencyOfPayment.name());
    }

    biddingDocument.setBidPackagePrice(request.getBidPackagePrice());
    biddingDocument.setBidFloorPrice(request.getBidFloorPrice());
    biddingDocument.setPriceLeadership(request.getPriceLeadership());

    biddingDocumentRepository.save(biddingDocument);
    return biddingDocument;
  }

  @Override
  public BiddingDocument editBiddingDocument(Long id, Map<String, Object> updates) {
    BiddingDocument biddingDocument = biddingDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));

    String bidOpening = (String) updates.get("bidOpening");
    if (bidOpening != null) {
      LocalDateTime bidOpeningTime = Tool.convertToLocalDateTime(bidOpening);
      biddingDocument.setBidOpening(bidOpeningTime);
    }

    String bidClosing = (String) updates.get("bidClosing");
    if (bidClosing != null) {
      LocalDateTime bidClosingTime = Tool.convertToLocalDateTime(bidClosing);
      biddingDocument.setBidClosing(bidClosingTime);
    }

    String currency = (String) updates.get("currentOfPayment");
    if (currency != null) {
      EnumCurrency currencyOfPayment = EnumCurrency.findByName(currency);
      if (currencyOfPayment == null) {
        currencyOfPayment = EnumCurrency.VND;
      }
      biddingDocument.setCurrencyOfPayment(currencyOfPayment.name());
    } else {
      biddingDocument.setCurrencyOfPayment(EnumCurrency.VND.name());
    }

    try {
      String packagePriceString = (String) updates.get("bidPackagePrice");
      if (packagePriceString != null) {
        Double bidPackagePrice = Double.parseDouble(packagePriceString);
        biddingDocument.setBidPackagePrice(bidPackagePrice);
      }

      String floorPriceString = (String) updates.get("bidFloorPrice");
      if (floorPriceString != null) {
        Double bidFloorPrice = Double.parseDouble(floorPriceString);
        biddingDocument.setBidFloorPrice(bidFloorPrice);
      }

      String priceLeadershipString = (String) updates.get("priceLeadership");
      if (priceLeadershipString != null) {
        Double priceLeadership = Double.parseDouble(priceLeadershipString);
        biddingDocument.setPriceLeadership(priceLeadership);
      }
    } catch (Exception e) {
      throw new InternalException("Parameters must be double.");
    }

    biddingDocumentRepository.save(biddingDocument);
    return biddingDocument;
  }

  @Override
  public void removeBiddingDocument(Long id) {
    BiddingDocument biddingDocument = biddingDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Bidding document is not found"));
    Outbound outbound = biddingDocument.getOutbound();
    if (outbound.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())
        || outbound.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())) {
      throw new InternalException("Bidding document is in a transaction.");
    }
    biddingDocumentRepository.deleteById(id);
  }

}
