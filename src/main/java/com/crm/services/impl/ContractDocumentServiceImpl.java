package com.crm.services.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumContractDocumentStatus;
import com.crm.enums.EnumShippingStatus;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Contract;
import com.crm.models.ContractDocument;
import com.crm.models.Supplier;
import com.crm.models.User;
import com.crm.payload.request.ContractDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ContractDocumentRepository;
import com.crm.repository.ContractRepository;
import com.crm.repository.ShippingInfoRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;
import com.crm.services.ContractDocumentService;
import com.crm.specification.builder.ContractDocumentSpecificationsBuilder;
import com.crm.websocket.controller.NotificationBroadcast;

@Service
public class ContractDocumentServiceImpl implements ContractDocumentService {

  @Autowired
  private ContractDocumentRepository contractDocumentRepository;

  @Autowired
  private ContractRepository contractRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ShippingInfoRepository shippingInfoRepository;

  @Autowired
  private NotificationBroadcast notificationBroadcast;

  @Override
  public ContractDocument createContractDocument(Long id, String username, ContractDocumentRequest request) {
    ContractDocument contractDocument = new ContractDocument();

    Contract contract = contractRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.COMBINED_NOT_FOUND));
    contractDocument.setContract(contract);

    Combined combined = contract.getCombined();
    Bid bid = combined.getBid();
    Supplier bidder = bid.getBidder();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();
    if (username.equals(bidder.getUsername()) || username.equals(offeree.getUsername())) {
      Supplier supplier = supplierRepository.findByUsername(username)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.SENDER_NOT_FOUND));
      contractDocument.setSender(supplier);

      if (!Tool.isBlank(request.getDocumentPath())) {
        contractDocument.setDocumentPath(request.getDocumentPath());
      } else {
        throw new InternalException(ErrorMessage.EVIDENCE_INVALID);
      }
      contractDocument.setStatus(EnumContractDocumentStatus.PENDING.name());
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    ContractDocument _evidence = contractDocumentRepository.save(contractDocument);
    notificationBroadcast.broadcastCreateContractDocumentToMerchant(contract);
    return _evidence;
  }

  @Override
  public Page<ContractDocument> getContractDocumentsByUser(String username, PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ContractDocument> contractDocuments = contractDocumentRepository.findByUser(username, page);
    return contractDocuments;
  }

  @Override
  public Page<ContractDocument> getContractDocumentsByContract(Long id, String username, PaginationRequest request) {
    if (!contractRepository.existsById(id)) {
      throw new NotFoundException(ErrorMessage.CONTRACT_NOT_FOUND);
    }
    Page<ContractDocument> contractDocuments = null;
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    String role = user.getRoles().iterator().next().getName();

    if (role.equalsIgnoreCase("ROLE_MODERATOR")) {
      contractDocuments = contractDocumentRepository.findByContract(id, page);
    } else {
      contractDocuments = contractDocumentRepository.findByContract(id, username, page);
    }
    return contractDocuments;
  }

  @Override
  public Page<ContractDocument> searchContractDocuments(PaginationRequest request, String search) {
    // Extract data from search string
    ContractDocumentSpecificationsBuilder builder = new ContractDocumentSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<ContractDocument> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<ContractDocument> pages = contractDocumentRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public ContractDocument editContractDocument(Long id, String username, Map<String, Object> updates) {
    ContractDocument contractDocument = contractDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.EVIDENCE_NOT_FOUND));
    Contract contract = contractDocument.getContract();
    Combined combined = contract.getCombined();
    Bid bid = combined.getBid();
    Supplier bidder = bid.getBidder();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();

    if (!username.equals(bidder.getUsername()) && !username.equals(offeree.getUsername())) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    if (username.equals(contractDocument.getSender().getUsername())) {
      // YOU CANNOT SET EVIDEN TO VALID BY YOURSELF
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    } else {
      String status = String.valueOf(updates.get("status"));
      if (updates.get("status") != null && !Tool.isEqual(contractDocument.getStatus(), status)) {
        contractDocument.setStatus(EnumContractDocumentStatus.findByName(status).name());
      }
    }

    ContractDocument _evidence = contractDocumentRepository.save(contractDocument);

    if (contract.getRequired() == true
        && contractDocumentRepository.existsValidContractDocument(id, EnumContractDocumentStatus.ACCEPTED.name())
        && contractDocument.getStatus().equals(EnumContractDocumentStatus.ACCEPTED.name())) {
      contract.getShippingInfos().forEach(item -> {
        item.setStatus(EnumShippingStatus.INFO_RECEIVED.name());
        shippingInfoRepository.save(item);
      });
      notificationBroadcast.broadcastCreateContractToShippingLine(contract);
      notificationBroadcast.broadcastCreateContractToDriver(contract);
    }

    if (contractDocument.getStatus().equals(EnumContractDocumentStatus.ACCEPTED.name())) {
      notificationBroadcast.broadcastAcceptContractDocumentToForwarder(contract);
    } else if (contractDocument.getStatus().equals(EnumContractDocumentStatus.REJECTED.name())) {
      notificationBroadcast.broadcastRejectContractDocumentToForwarder(contract);
    }

    return _evidence;
  }

  @Override
  public void removeContractDocument(Long id, String username) {
    ContractDocument contractDocument = contractDocumentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.EVIDENCE_NOT_FOUND));
    Contract contract = contractDocument.getContract();
    Combined combined = contract.getCombined();

    Bid bid = combined.getBid();
    Supplier bidder = bid.getBidder();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();
    if (username.equals(bidder.getUsername()) || username.equals(offeree.getUsername())) {
      contractDocumentRepository.deleteById(id);
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

  }
}
