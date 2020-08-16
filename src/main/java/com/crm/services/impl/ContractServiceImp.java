package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.Contract;
import com.crm.models.Discount;
import com.crm.models.Supplier;
import com.crm.payload.request.ContractRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContractRepository;
import com.crm.repository.DiscountRepository;
import com.crm.repository.SupplierRepository;
import com.crm.services.BidService;
import com.crm.services.ContractService;
import com.crm.services.ShippingInfoService;
import com.crm.specification.builder.ContractSpecificationsBuilder;
import com.crm.websocket.controller.NotificationBroadcast;

@Service
public class ContractServiceImp implements ContractService {

  @Autowired
  private ContractRepository contractRepository;

  @Autowired
  private CombinedRepository combinedRepository;

  @Autowired
  private DiscountRepository discountRepository;

  @Autowired
  private BidService bidService;

  @Autowired
  private ShippingInfoService shippingInfoService;

  @Autowired
  private NotificationBroadcast notificationBroadcast;

  @Autowired
  private SupplierRepository supplierDtoRepository;

  @Override
  public Contract createContract(Long id, String username, ContractRequest request) {
    Contract contract = new Contract();

    Combined combined = combinedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.COMBINED_NOT_FOUND));
    contract.setCombined(combined);

    Supplier sender = supplierDtoRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    contract.setSender(sender);

    List<Long> containersId = new ArrayList<>();
    Bid bid = combined.getBid();
    List<Container> containers = new ArrayList<Container>(bid.getContainers());
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    if (!biddingDocument.getIsMultipleAward()) {
      for (Container container : containers) {
        containersId.add(container.getId());
      }
    } else {
      containersId = request.getContainers();
    }

    if (containersId == null || containersId.size() == 0) {
      throw new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND);
    }

    Double price = (bid.getBidPrice() / bid.getContainers().size()) * containersId.size();

    contract.setPrice(price);
    Supplier offeree = biddingDocument.getOfferee();
    Boolean required = request.getRequired();
    contract.setRequired(required);
    contract.setFinesAgainstContractViolations(0D);
    contract.setCreationDate(LocalDateTime.now());

    String discountCodeString = request.getDiscountCode();
    if (discountCodeString != null && !discountCodeString.isEmpty()) {
      Discount discount = discountRepository.findByCode(discountCodeString)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.DISCOUNT_NOT_FOUND));
      contract.setDiscount(discount);
    }
    if (username.equals(offeree.getUsername()) && required == true) {
      Double fines = request.getFinesAgainstContractViolations();
      if (fines > 0) {
        contract.setFinesAgainstContractViolations(fines);
      } else {
        throw new InternalException(ErrorMessage.CONTRACT_INVALID_FINES);
      }
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    Contract _contract = contractRepository.save(contract);

    bid = bidService.editBidWhenCombined(bid.getId(), username, containersId);

    shippingInfoService.createShippingInfosForContract(contract, containersId);

    // CREATE NOTIFICATION
    notificationBroadcast.broadcastCreateContractToDriver(contract);
    // END NOTIFICATION
    return _contract;
  }

  @Override
  public Contract getContractByCombined(Long id, String username) {
    Combined combined = combinedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.COMBINED_NOT_FOUND));
    Bid bid = combined.getBid();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();
    Contract contract = null;
    if (username.equals(offeree.getUsername()) || username.equals(bid.getBidder().getUsername())) {
      contract = contractRepository.findByCombined(id, username)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTRACT_NOT_FOUND));
    }
    return contract;
  }

  @Override
  public Page<Contract> getContractsByUser(String username, PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Contract> contracts = contractRepository.findByUser(username, page);
    return contracts;
  }

  @Override
  public Page<Contract> searchContracts(PaginationRequest request, String search) {
    // Extract data from search string
    ContractSpecificationsBuilder builder = new ContractSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Contract> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Contract> pages = contractRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public Contract editContract(Long id, String username, Map<String, Object> updates) {
    Contract contract = contractRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTRACT_NOT_FOUND));
    Combined combined = contract.getCombined();

    Bid bid = combined.getBid();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();

    if (username.equals(offeree.getUsername())) {
      String requiredString = String.valueOf(updates.get("required"));
      if (updates.get("required") != null && !Tool.isEqual(contract.getRequired(), requiredString)) {
        contract.setRequired(Boolean.valueOf(requiredString));
      }
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    Contract _contract = contractRepository.save(contract);
    return _contract;
  }

  @Override
  public void removeContract(Long id, String username) {
    Contract contract = contractRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTRACT_NOT_FOUND));
    Combined combined = contract.getCombined();

    Bid bid = combined.getBid();
    Supplier bidder = bid.getBidder();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();
    if (username.equals(bidder.getUsername()) || username.equals(offeree.getUsername())) {
      contractRepository.deleteById(id);
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

  }

}
