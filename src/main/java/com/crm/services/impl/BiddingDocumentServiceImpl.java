package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
import com.crm.enums.EnumCurrency;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.BiddingDocument;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.User;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContainerRepository;
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
  private ContainerRepository containerRepository;

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private CombinedRepository combinedRepository;

  @Override
  public BiddingDocument createBiddingDocument(String username, BiddingDocumentRequest request) {
    BiddingDocument biddingDocument = new BiddingDocument();

    Merchant merchant = merchantRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));
    biddingDocument.setOfferee(merchant);

    Outbound outbound = outboundRepository.findById(request.getOutbound())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.OUTBOUND_NOT_FOUND));
    if (merchant.getOutbounds().contains(outbound)) {
      if (outbound.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
          || outbound.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(ErrorMessage.OUTBOUND_IS_IN_TRANSACTION);
      }
      outbound.setStatus(EnumSupplyStatus.BIDDING.name());
      biddingDocument.setOutbound(outbound);
    } else {
      throw new NotFoundException(ErrorMessage.OUTBOUND_IS_NOT_YOUR);
    }

    biddingDocument.setIsMultipleAward(request.getIsMultipleAward());

    biddingDocument.setBidOpening(LocalDateTime.now());

    LocalDateTime packingTime = outbound.getPackingTime();
    LocalDateTime bidClosing = Tool.convertToLocalDateTime(request.getBidClosing());
    if (bidClosing.isBefore(LocalDateTime.now()) || bidClosing.isAfter(packingTime)) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_INVALID_CLOSING_TIME);
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

    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    BiddingDocument _biddingDocument = biddingDocumentRepository.save(biddingDocument);
    return _biddingDocument;
  }

  @Override
  public BiddingDocument getBiddingDocument(Long id) {
    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument = biddingDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));
    return biddingDocument;
  }

  @Override
  public BiddingDocument getBiddingDocumentByBid(Long id, String username) {
    if (!bidRepository.existsById(id)) {
      throw new NotFoundException(ErrorMessage.BID_NOT_FOUND);
    }
    BiddingDocument biddingDocument = biddingDocumentRepository.findByBid(id, username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));
    return biddingDocument;
  }

  @Override
  public BiddingDocument getBiddingDocumentByCombined(Long id, String username) {
    if (!combinedRepository.existsById(id)) {
      throw new NotFoundException(ErrorMessage.COMBINED_NOT_FOUND);
    }
    BiddingDocument biddingDocument = biddingDocumentRepository.findByCombined(id, username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));
    return biddingDocument;
  }

  @Override
  public Page<BiddingDocument> getBiddingDocumentsByExistCombined(String username, PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<BiddingDocument> biddingDocuments = biddingDocumentRepository.findByExistCombined(username, page);
    return biddingDocuments;
  }

  @Override
  public Page<BiddingDocument> getBiddingDocuments(String username, PaginationRequest request) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    String status = request.getStatus();
    Page<BiddingDocument> biddingDocuments = null;
    if (user.getRoles().iterator().next().getName().equalsIgnoreCase("ROLE_MERCHANT")) {
      if (status != null && !status.isEmpty()) {
        biddingDocuments = biddingDocumentRepository.findByMerchant(username, request.getStatus(),
            PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
      } else {
        biddingDocuments = biddingDocumentRepository.findByMerchant(username,
            PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
      }
    }
    if (user.getRoles().iterator().next().getName().equalsIgnoreCase("ROLE_FORWARDER")) {
      if (status != null && !status.isEmpty()) {
        biddingDocuments = biddingDocumentRepository.findByForwarder(username, request.getStatus(),
            PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
      } else {
        biddingDocuments = biddingDocumentRepository.findByForwarder(username,
            PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
      }
    }
    return biddingDocuments;
  }

  @Override
  public BiddingDocument updateBiddingDocument(BiddingDocumentRequest request) {
    BiddingDocument biddingDocument = biddingDocumentRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));

    if (biddingDocument.getStatus().equalsIgnoreCase(EnumBiddingStatus.COMBINED.name())) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_IS_IN_TRANSACTION);
    }

    Outbound outbound = biddingDocument.getOutbound();
    LocalDateTime packingTime = outbound.getPackingTime();
    LocalDateTime bidClosingTime = Tool.convertToLocalDateTime(request.getBidClosing());
    if (bidClosingTime.isBefore(LocalDateTime.now()) || bidClosingTime.isAfter(packingTime)) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_INVALID_CLOSING_TIME);
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

    BiddingDocument _biddingDocument = biddingDocumentRepository.save(biddingDocument);
    return _biddingDocument;
  }

  @Override
  public BiddingDocument editBiddingDocument(Long id, Map<String, Object> updates) {
    BiddingDocument biddingDocument = biddingDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));

    if (biddingDocument.getStatus().equalsIgnoreCase(EnumBiddingStatus.COMBINED.name())) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_IS_IN_TRANSACTION);
    }
    Outbound outbound = biddingDocument.getOutbound();
    LocalDateTime packingTime = outbound.getPackingTime();

    String bidClosing = String.valueOf(updates.get("bidClosing"));
    if (updates.get("bidClosing") != null && !Tool.isBlank(bidClosing)) {
      LocalDateTime bidClosingTime = Tool.convertToLocalDateTime(bidClosing);
      if (bidClosingTime.isBefore(LocalDateTime.now()) || bidClosingTime.isAfter(packingTime)) {
        throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_INVALID_CLOSING_TIME);
      }
      biddingDocument.setBidClosing(bidClosingTime);
    }

    String currency = String.valueOf(updates.get("currentOfPayment"));
    if (updates.get("currentOfPayment") != null && !Tool.isEqual(biddingDocument.getCurrencyOfPayment(), currency)) {
      EnumCurrency currencyOfPayment = EnumCurrency.findByName(currency);
      if (currencyOfPayment == null) {
        currencyOfPayment = EnumCurrency.VND;
      }
      biddingDocument.setCurrencyOfPayment(currencyOfPayment.name());
    }

    String packagePriceString = String.valueOf(updates.get("bidPackagePrice"));
    if (updates.get("bidPackagePrice") != null
        && !Tool.isEqual(biddingDocument.getBidPackagePrice(), packagePriceString)) {
      biddingDocument.setBidPackagePrice(Double.parseDouble(packagePriceString));
    }

    String floorPriceString = String.valueOf(updates.get("bidFloorPrice"));
    if (updates.get("bidFloorPrice") != null && !Tool.isEqual(biddingDocument.getBidFloorPrice(), floorPriceString)) {
      biddingDocument.setBidFloorPrice(Double.parseDouble(floorPriceString));
    }

    String status = String.valueOf(updates.get("status"));
    EnumBiddingStatus eStatus = null;
    if (updates.get("status") != null && !Tool.isBlank(status)
        && (eStatus = EnumBiddingStatus.findByName(status)) != null) {
      biddingDocument.setStatus(eStatus.name());
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

    BiddingDocument _biddingDocument = biddingDocumentRepository.save(biddingDocument);
    return _biddingDocument;
  }

  @Override
  public void removeBiddingDocument(Long id, String username) {
    Merchant merchant = merchantRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.MERCHANT_NOT_FOUND));
    BiddingDocument biddingDocument = biddingDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));
    if (!biddingDocument.getOfferee().equals(merchant)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    if (!biddingDocument.getStatus().equalsIgnoreCase(EnumBiddingStatus.CANCELED.name())) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_IS_IN_TRANSACTION);
    }
    biddingDocumentRepository.deleteById(id);
  }

}
