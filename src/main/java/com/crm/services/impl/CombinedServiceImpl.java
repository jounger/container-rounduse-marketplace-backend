package com.crm.services.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorMessage;
import com.crm.common.NotificationMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
import com.crm.enums.EnumCombinedNotification;
import com.crm.enums.EnumInvoiceType;
import com.crm.enums.EnumNotificationType;
import com.crm.enums.EnumShippingStatus;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Contract;
import com.crm.models.Outbound;
import com.crm.models.ShippingInfo;
import com.crm.models.Supplier;
import com.crm.models.User;
import com.crm.payload.request.CombinedNotificationRequest;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.ContractRequest;
import com.crm.payload.request.InvoiceRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingInfoRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContractRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.UserRepository;
import com.crm.services.CombinedService;
import com.crm.services.ContractService;
import com.crm.services.InvoiceService;
import com.crm.services.ShippingInfoService;
import com.crm.websocket.controller.NotificationBroadcast;

@Service
public class CombinedServiceImpl implements CombinedService {

  @Autowired
  private CombinedRepository combinedRepository;

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private OutboundRepository outboundRepository;

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private BiddingDocumentRepository biddingDocumentRepository;

  @Autowired
  private ContractRepository contractRepository;

  @Autowired
  private ContractService contractService;

  @Autowired
  private InvoiceService invoiceService;

  @Autowired
  private ShippingInfoService shippingInfoService;

  @Autowired
  private NotificationBroadcast notificationBroadcast;

  @Override
  public Combined createCombined(Long bidId, String username, CombinedRequest request) {
    Combined combined = new Combined();

    Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new NotFoundException(ErrorMessage.BID_NOT_FOUND));
    if (bid.getCombined() != null) {
      throw new DuplicateRecordException(ErrorMessage.BID_INVALID_CREATE);
    }
    if (bid.getStatus().equals(EnumBidStatus.EXPIRED.name())) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_ACCEPT_INVALID_BID);
    }
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    if (biddingDocument.getBidClosing().isBefore(LocalDateTime.now())) {
      throw new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_TIME_OUT);
    }

    combined.setBid(bid);
    combined.setIsCanceled(false);

    Combined _combined = combinedRepository.save(combined);
    ContractRequest contractRequest = request.getContract();

    Contract contract = contractService.createContract(combined.getId(), username, contractRequest);
    combined.setContract(contract);
    contract.setCombined(combined);

    return _combined;
  }

  @Override
  public Page<Combined> getCombinedsByBiddingDocument(Long id, String username, PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Combined> combines = combinedRepository.findByBiddingDocument(id, username, page);
    return combines;
  }

  @Override
  public Combined getCombined(Long id) {
    Combined combined = combinedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.COMBINED_NOT_FOUND));
    return combined;
  }

  @Override
  public Page<Combined> getCombinedsByUser(String username, PaginationRequest request) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    String role = user.getRoles().iterator().next().getName();
    Page<Combined> combineds = null;
    if (role.equalsIgnoreCase("ROLE_MERCHANT")) {
      combineds = combinedRepository.findByMerchant(username,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else if (role.equalsIgnoreCase("ROLE_FORWARDER")) {
      combineds = combinedRepository.findByForwarder(username,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    return combineds;
  }

  @Override
  public Page<Combined> getCombineds(PaginationRequest request) {
    Page<Combined> combineds = combinedRepository
        .findAll(PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    return combineds;
  }

  @Override
  public Combined editCombined(Long id, String username, String isCanceled) {
    Combined combined = combinedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.COMBINED_NOT_FOUND));

    Bid bid = combined.getBid();
    if (!Tool.isBlank(isCanceled)) {

      // Create Invoice when Canceled invoice
      Contract contract = combined.getContract();

      Collection<ShippingInfo> shippingInfos = new ArrayList<>();
      shippingInfos = contract.getShippingInfos();
      String forwarderUsername = contract.getCombined().getBid().getBidder().getUsername();
      if (shippingInfos != null && shippingInfos.size() > 0) {
        ShippingInfoRequest shippingInfoRequest = new ShippingInfoRequest();
        shippingInfos.forEach(shippingInfo -> {
          if (shippingInfo.getStatus().equals(EnumShippingStatus.SHIPPING.name())
              || shippingInfo.getStatus().equals(EnumShippingStatus.DELIVERED.name())) {
            throw new InternalException(ErrorMessage.COMBINED_INVALID_CANCEL);
          }
          shippingInfoRequest.setStatus(EnumShippingStatus.EXCEPTION.name());
          shippingInfoService.editStatusShippingInfoToExpided(shippingInfo.getId(), forwarderUsername,
              shippingInfoRequest);
        });
      }

      if (contract.getRequired()) {
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setType(EnumInvoiceType.FINES.name());

        Double amount = (contract.getFinesAgainstContractViolations() / 100) * contract.getPrice();
        invoiceRequest.setAmount(amount);

        invoiceService.createInvoice(contract.getId(), username, invoiceRequest);
      }

      combined.setIsCanceled(Boolean.valueOf(isCanceled));

      BiddingDocument biddingDocument = bid.getBiddingDocument();
      Outbound outbound = biddingDocument.getOutbound();
      if (biddingDocument.getBidClosing().isBefore(LocalDateTime.now())) {
        biddingDocument.setStatus(EnumBiddingStatus.EXPIRED.name());
        outbound.setStatus(EnumSupplyStatus.CREATED.name());
        outboundRepository.save(outbound);

        biddingDocument.getBids().forEach(bidEdit -> {
          if (bidEdit.getStatus().equals(EnumBidStatus.ACCEPTED.name())) {
            bidEdit.setStatus(EnumBidStatus.REJECTED.name());
            bidEdit.setDateOfDecision(LocalDateTime.now());
            bidEdit.getContainers().forEach(container -> {
              container.setStatus(EnumSupplyStatus.CREATED.name());
              containerRepository.save(container);
            });
            bidRepository.save(bidEdit);
          }
        });
        biddingDocumentRepository.save(biddingDocument);
      } else {

        bid.setStatus(EnumBidStatus.REJECTED.name());
        bid.setDateOfDecision(LocalDateTime.now());
        bid.getContainers().forEach(container -> {
          container.setStatus(EnumSupplyStatus.CREATED.name());
          containerRepository.save(container);
        });
        bidRepository.save(bid);

        biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());
        biddingDocumentRepository.save(biddingDocument);

        outbound.setStatus(EnumSupplyStatus.BIDDING.name());
        outboundRepository.save(outbound);

      }

      Double percent = 100D;
      NumberFormat numberFormat = new DecimalFormat(Constant.CONTRACT_PAID_PERCENTAGE_FORMAT);
      contract.setPaymentPercentage(Double.valueOf(numberFormat.format(percent)));
      contractRepository.save(contract);

      Supplier merchant = contract.getSender();
      Supplier forwarder = combined.getBid().getBidder();
      CombinedNotificationRequest notifyRequest = new CombinedNotificationRequest();
      notifyRequest.setRelatedResource(combined.getId());
      notifyRequest.setAction(EnumCombinedNotification.CANCEL.name());
      notifyRequest.setType(EnumNotificationType.COMBINED.name());

      // send notification to forwarder or merchant
      if (username.equals(merchant.getUsername())) {
        notifyRequest.setRecipient(forwarder.getUsername());
        notifyRequest.setMessage(
            String.format(NotificationMessage.SEND_CANCEL_COMBINED_NOTIFICATION, merchant.getCompanyName()));

      } else if (username.equals(forwarder.getUsername())) {
        notifyRequest.setRecipient(merchant.getUsername());
        notifyRequest.setMessage(
            String.format(NotificationMessage.SEND_CANCEL_COMBINED_NOTIFICATION, forwarder.getCompanyName()));

      } else {
        throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
      }
      notificationBroadcast.broadcastSendCombinedNotificationToUser(notifyRequest);

      // send notification to shippingline
      String shippingLine = biddingDocument.getOutbound().getShippingLine().getUsername();
      notifyRequest.setRecipient(shippingLine);
      notificationBroadcast.broadcastSendCombinedNotificationToUser(notifyRequest);

      // send notification to driver
      notificationBroadcast.broadcastSendCombinedNotificationToDriver(contract, notifyRequest);
    }
    Combined _combined = combinedRepository.save(combined);
    return _combined;
  }

  @Override
  public void removeCombined(Long id) {
    if (combinedRepository.existsById(id)) {
      combinedRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorMessage.COMBINED_NOT_FOUND);
    }
  }

}
