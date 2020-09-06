package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BillOfLading;
import com.crm.models.Inbound;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.User;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.InboundRepository;
import com.crm.repository.InvoiceRepository;
import com.crm.repository.MerchantRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.UserRepository;
import com.crm.services.BidService;
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

  @Autowired
  private InboundRepository inboundRepository;

  @Autowired
  private InvoiceRepository invoiceRepository;

  @Autowired
  private BidService bidService;

  @Override
  public BiddingDocument createBiddingDocument(String username, BiddingDocumentRequest request) {
    BiddingDocument biddingDocument = new BiddingDocument();

    LocalDateTime paymentTerm = LocalDateTime.now().minusDays(45);
    Boolean invoices = invoiceRepository.checkInvoicePaymentDateAndIsPaid(username, paymentTerm);
    if (!invoices) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_CANNOT_CREATE_INVOICE);
    }

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
    if (packingTime.isBefore(LocalDateTime.now())) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_INVALID_OPENING_TIME);
    }
    LocalDateTime bidClosing = Tool.convertToLocalDateTime(request.getBidClosing());
    if (bidClosing.isAfter(packingTime) || bidClosing.isBefore(LocalDateTime.now())) {
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
    if (request.getIsMultipleAward()) {
      biddingDocument.setBidFloorPrice(0D);
    } else {
      biddingDocument.setBidFloorPrice(request.getBidFloorPrice());
    }
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
  public BiddingDocument editBiddingDocument(Long id, String username, Map<String, Object> updates) {
    BiddingDocument biddingDocument = biddingDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));
    if (!(biddingDocument.getOfferee().getUsername().equals(username)
        || biddingDocumentRepository.isBidderByBiddingDocument(id, username))) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    if (biddingDocument.getStatus().equalsIgnoreCase(EnumBiddingStatus.COMBINED.name())) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_IS_IN_TRANSACTION);
    }

    Outbound outbound = biddingDocument.getOutbound();

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
      if (eStatus.name().equalsIgnoreCase(EnumBiddingStatus.EXPIRED.name())) {
        outbound = biddingDocument.getOutbound();
        outbound.setStatus(EnumSupplyStatus.CREATED.name());
        outboundRepository.save(outbound);

        biddingDocument.getBids().forEach(bid -> {
          if (bid.getStatus().equals(EnumBidStatus.ACCEPTED.name())) {
            bid.setStatus(EnumBidStatus.REJECTED.name());
            bid.setDateOfDecision(LocalDateTime.now());
            bid.getContainers().forEach(container -> {
              container.setStatus(EnumSupplyStatus.CREATED.name());
              containerRepository.save(container);
            });
            bidRepository.save(bid);
          }
        });
      }
    }

    if (biddingDocument.getStatus().equalsIgnoreCase(EnumBiddingStatus.COMBINED.name())) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_IS_IN_TRANSACTION);
    }

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

    BiddingDocument _biddingDocument = biddingDocumentRepository.save(biddingDocument);
    return _biddingDocument;
  }

  @Override
  public void removeBiddingDocument(Long id, String username) {
    BiddingDocument biddingDocument = biddingDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));
    if (!biddingDocument.getOfferee().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    if (!(biddingDocument.getStatus().equalsIgnoreCase(EnumBiddingStatus.CANCELED.name())
        || biddingDocument.getStatus().equalsIgnoreCase(EnumBiddingStatus.EXPIRED.name()))) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_IS_IN_TRANSACTION);
    }
    biddingDocumentRepository.deleteById(id);
  }

  @Override
  public Page<BiddingDocument> getBiddingDocumentsByInbound(Long id, String username, PaginationRequest request) {
    Inbound inbound = inboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.INBOUND_NOT_FOUND));
    if (!inbound.getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    BillOfLading billOfLading = inbound.getBillOfLading();

    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by("createdAt").descending());
    Page<BiddingDocument> pages = biddingDocumentRepository.findByInbound(inbound.getShippingLine().getCompanyCode(),
        inbound.getContainerType().getName(), Arrays.asList("BIDDING"), inbound.getEmptyTime(),
        billOfLading.getFreeTime(), page);

    return pages;
  }

  @Override
  public void updateExpiredBiddingDocuments(Long id, String status) {
    BiddingDocument biddingDocument = biddingDocumentRepository.getOne(id);
    Collection<Bid> bids = new ArrayList<>();
    bids = biddingDocument.getBids();
    biddingDocument.setStatus(status);
    Outbound outbound = biddingDocument.getOutbound();
    String bidStatus = EnumBidStatus.EXPIRED.name();
    // if status equal EXPIRED set outbound's status to CREATED
    if (status.equalsIgnoreCase(EnumBiddingStatus.EXPIRED.name())) {
      outbound.setStatus(EnumSupplyStatus.CREATED.name());
      outboundRepository.save(outbound);
    }
    // if status equal COMBINED set outbound's status to COMBINED
    if (status.equalsIgnoreCase(EnumBiddingStatus.COMBINED.name())) {
      outbound = biddingDocument.getOutbound();
      outbound.setStatus(EnumSupplyStatus.COMBINED.name());
      outboundRepository.save(outbound);
    }
    if (!bids.isEmpty()) {
      for (Bid bid : bids) {
        if (bid.getStatus().equals(EnumBidStatus.PENDING.name())) {
          bidService.editExpiredBids(bid, bidStatus);
        }
      }
    }
    biddingDocumentRepository.save(biddingDocument);
  }

  @Override
  public List<BiddingDocument> updateExpiredBiddingDocumentFromList(List<BiddingDocument> biddingDocuments) {
    List<BiddingDocument> result = new ArrayList<BiddingDocument>();

    for (BiddingDocument biddingDocument : biddingDocuments) {
      String status = biddingDocument.getStatus();
      boolean existsCombinedBid = biddingDocumentRepository.existsCombinedBid(biddingDocument.getId());
      if (biddingDocument.getBidClosing().isBefore(LocalDateTime.now())
          && biddingDocument.getStatus().equals(EnumBiddingStatus.BIDDING.name())) {
        if (!existsCombinedBid) {
          status = EnumBiddingStatus.EXPIRED.name();
        } else if (existsCombinedBid) {
          status = EnumBiddingStatus.COMBINED.name();
        }
        updateExpiredBiddingDocuments(biddingDocument.getId(), status);
        biddingDocument.setStatus(status);
      }
      result.add(biddingDocument);
    }
    return result;
  }

}
