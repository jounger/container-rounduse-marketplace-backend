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
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.Contract;
import com.crm.models.Supplier;
import com.crm.payload.request.ContractRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContractRepository;
import com.crm.repository.SupplierRepository;
import com.crm.services.BidService;
import com.crm.services.ContractService;
import com.crm.services.ShippingInfoService;
import com.crm.specification.builder.ContractSpecificationsBuilder;

@Service
public class ContractServiceImp implements ContractService {

  @Autowired
  private ContractRepository contractRepository;

  @Autowired
  private CombinedRepository combinedRepository;

  @Autowired
  private BidService bidService;

  @Autowired
  private ShippingInfoService shippingInfoService;

  @Autowired
  private SupplierRepository supplierDtoRepository;

  @Override
  public Contract createContract(Long id, String username, ContractRequest request) {
    Contract contract = new Contract();

    Combined combined = combinedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.COMBINED_NOT_FOUND));
    contract.setCombined(combined);

    Bid bid = combined.getBid();
    BiddingDocument biddingDocument = bid.getBiddingDocument();

    Supplier offeree = biddingDocument.getOfferee();
    if (!username.equals(offeree.getUsername())) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    Supplier sender = supplierDtoRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    contract.setSender(sender);

    List<Long> containersId = new ArrayList<>();
    List<Container> containers = new ArrayList<Container>(bid.getContainers());

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
    Double price = 0D;
    if (request.getPrice() == null) {
      price = (bid.getBidPrice() / bid.getContainers().size()) * containersId.size() * 100;
    }
    contract.setPrice(price);

    Boolean required = request.getRequired();
    contract.setRequired(required);
    contract.setFinesAgainstContractViolations(0D);
    contract.setCreationDate(LocalDateTime.now());
    contract.setPaidPercentage(0D);

//    String discountCodeString = request.getDiscountCode();
//    if (!Tool.isBlank(discountCodeString)) {
//      Discount discount = discountRepository.findByCode(discountCodeString)
//          .orElseThrow(() -> new NotFoundException(ErrorMessage.DISCOUNT_NOT_FOUND));
//      contract.setDiscount(discount);
//    }

    Double fines = 0D;
    if (required == true) {
      fines = request.getFinesAgainstContractViolations();
    }
    contract.setFinesAgainstContractViolations(fines);

    Contract _contract = contractRepository.save(contract);

    bid = bidService.editBidWhenCombined(bid.getId(), username, containersId);

    shippingInfoService.createShippingInfosForContract(contract, containersId);

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

    if (!contractRepository.isUnpaidContract(id)) {
      throw new ForbiddenException(ErrorMessage.CONTRACT_INVALID_EDIT);
    }

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
    String priceString = String.valueOf(updates.get("price"));
    if (priceString != null && Tool.isEqual(contract.getPrice(), priceString)) {
      contract.setPrice(Double.valueOf(priceString));
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
