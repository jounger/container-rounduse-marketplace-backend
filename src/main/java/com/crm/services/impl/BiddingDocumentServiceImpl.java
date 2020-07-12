package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
import com.crm.enums.EnumCurrency;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.BiddingDocument;
import com.crm.models.Discount;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.User;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.DiscountRepository;
import com.crm.repository.MerchantRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.UserRepository;
import com.crm.services.BiddingDocumentService;

@Service
public class BiddingDocumentServiceImpl implements BiddingDocumentService {

  @Autowired
  private BiddingDocumentRepository biddingDocumentRepository;

  @Autowired
  private MerchantRepository merchantRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private OutboundRepository outboundRepository;

  @Autowired
  private DiscountRepository discountRepository;

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private BidRepository bidRepository;

  @Override
  public BiddingDocument createBiddingDocument(Long id, BiddingDocumentRequest request) {
    BiddingDocument biddingDocument = new BiddingDocument();

    Merchant merchant = new Merchant();
    merchant = merchantRepository.findById(id).orElseThrow(() -> new NotFoundException("Merchant is not found"));
    biddingDocument.setOfferee(merchant);

    Outbound outbound = outboundRepository.findById(request.getOutbound())
        .orElseThrow(() -> new NotFoundException("Outbound is not found."));
    if (merchant.getOutbounds().contains(outbound)) {
      if (outbound.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
          || outbound.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
        throw new DuplicateRecordException("Outbound must be not in any transaction.");
      }
      outbound.setStatus(EnumSupplyStatus.BIDDING.name());
      biddingDocument.setOutbound(outbound);
    } else {
      throw new NotFoundException("This outbound must be your outbound.");
    }

    biddingDocument.setIsMultipleAward(request.getIsMultipleAward());

    biddingDocument.setBidOpening(LocalDateTime.now());

    LocalDateTime packingTime = outbound.getPackingTime();
    LocalDateTime bidClosing = Tool.convertToLocalDateTime(request.getBidClosing());
    if (bidClosing.isBefore(LocalDateTime.now()) || bidClosing.isAfter(packingTime)) {
      throw new InternalException("Bid closing time must be after now and before packing time.");
    }
    biddingDocument.setBidClosing(bidClosing);

    EnumCurrency currency = EnumCurrency.findByName(request.getCurrencyOfPayment());
    if (currency != null) {
      biddingDocument.setCurrencyOfPayment(currency.name());
    } else {
      biddingDocument.setCurrencyOfPayment(EnumCurrency.VND.name());
    }

    biddingDocument.setBidPackagePrice(request.getBidPackagePrice());
    biddingDocument.setBidFloorPrice(request.getBidFloorPrice());
    biddingDocument.setPriceLeadership(request.getBidPackagePrice());

    String discountCodeString = request.getBidDiscountCode();
    if (discountCodeString != null && !discountCodeString.isEmpty()) {
      Discount bidDiscountCode = discountRepository.findByCode(discountCodeString)
          .orElseThrow(() -> new NotFoundException("Discount is not found."));
      biddingDocument.setDiscount(bidDiscountCode);
    }

    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

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
  public Page<BiddingDocument> getBiddingDocuments(Long id, PaginationRequest request) {
    User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User is not found."));
    String status = request.getStatus();
    Page<BiddingDocument> biddingDocuments = null;
    if (user.getRoles().iterator().next().getName().equalsIgnoreCase("ROLE_MERCHANT")) {
      if (status != null && !status.isEmpty()) {
        biddingDocuments = biddingDocumentRepository.findByMerchant(id, request.getStatus(),
            PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
      } else {
        biddingDocuments = biddingDocumentRepository.findByMerchant(id,
            PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
      }
    }
    if (user.getRoles().iterator().next().getName().equalsIgnoreCase("ROLE_FORWARDER")) {
      if (status != null && !status.isEmpty()) {
        biddingDocuments = biddingDocumentRepository.findByForwarder(id, request.getStatus(),
            PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
      } else {
        biddingDocuments = biddingDocumentRepository.findByForwarder(id,
            PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
      }
    }
    return biddingDocuments;
  }

  @Override
  public BiddingDocument updateBiddingDocument(BiddingDocumentRequest request) {
    BiddingDocument biddingDocument = biddingDocumentRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));

    if (biddingDocument.getStatus().equalsIgnoreCase(EnumBiddingStatus.COMBINED.name())) {
      throw new InternalException("CAN NOT edit bidding document if it was combined.");
    }

    Outbound outbound = biddingDocument.getOutbound();
    LocalDateTime packingTime = outbound.getPackingTime();
    LocalDateTime bidClosingTime = Tool.convertToLocalDateTime(request.getBidClosing());
    if (bidClosingTime.isBefore(LocalDateTime.now()) || bidClosingTime.isAfter(packingTime)) {
      throw new InternalException("Bid closing time must be after now.");
    }
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

    if (biddingDocument.getStatus().equalsIgnoreCase(EnumBiddingStatus.COMBINED.name())) {
      throw new InternalException("CAN NOT edit bidding document if it was combined.");
    }
    Outbound outbound = biddingDocument.getOutbound();
    LocalDateTime packingTime = outbound.getPackingTime();

    String bidClosing = (String) updates.get("bidClosing");
    if (bidClosing != null && !bidClosing.isEmpty()) {
      LocalDateTime bidClosingTime = Tool.convertToLocalDateTime(bidClosing);
      if (bidClosingTime.isBefore(LocalDateTime.now()) || bidClosingTime.isAfter(packingTime)) {
        throw new InternalException("Bid closing time must be after now.");
      }
      biddingDocument.setBidClosing(bidClosingTime);
    }

    String currency = (String) updates.get("currentOfPayment");
    if (currency != null && !currency.isEmpty()) {
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
      if (packagePriceString != null && !packagePriceString.isEmpty()) {
        Double bidPackagePrice = Double.parseDouble(packagePriceString);
        biddingDocument.setBidPackagePrice(bidPackagePrice);
      }

      String floorPriceString = (String) updates.get("bidFloorPrice");
      if (floorPriceString != null && !floorPriceString.isEmpty()) {
        Double bidFloorPrice = Double.parseDouble(floorPriceString);
        biddingDocument.setBidFloorPrice(bidFloorPrice);
      }

      String priceLeadershipString = (String) updates.get("priceLeadership");
      if (priceLeadershipString != null && !priceLeadershipString.isEmpty()) {
        Double priceLeadership = Double.parseDouble(priceLeadershipString);
        biddingDocument.setPriceLeadership(priceLeadership);
      }
    } catch (Exception e) {
      throw new InternalException("Parameters must be double.");
    }

    String status = (String) updates.get("status");
    if (status != null && !status.isEmpty()) {
      EnumBiddingStatus eStatus = EnumBiddingStatus.findByName(status);
      if (eStatus != null) {
        biddingDocument.setStatus(eStatus.name());
      } else {
        throw new NotFoundException("Status is not found.");
      }
      if (eStatus.name().equalsIgnoreCase(EnumBiddingStatus.CANCELED.name())) {
        outbound = biddingDocument.getOutbound();
        outbound.setStatus(EnumSupplyStatus.CREATED.name());
        outboundRepository.save(outbound);

        biddingDocument.getBids().forEach(bid -> {
          bid.setStatus(EnumBidStatus.REJECTED.name());
          bid.setDateOfDecision(LocalDateTime.now());
          bid.getContainers().forEach(container -> {
            container.setStatus(EnumSupplyStatus.CREATED.name());
            containerRepository.save(container);
          });
          bidRepository.save(bid);
        });
      }
    }

    biddingDocumentRepository.save(biddingDocument);
    return biddingDocument;
  }

  @Override
  public void removeBiddingDocument(Long id) {
    BiddingDocument biddingDocument = biddingDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Bidding document is not found"));
    if (!biddingDocument.getStatus().equalsIgnoreCase(EnumBiddingStatus.CANCELED.name())) {
      throw new InternalException("Bidding document is in a transaction.");
    }
    biddingDocumentRepository.deleteById(id);
  }

}
