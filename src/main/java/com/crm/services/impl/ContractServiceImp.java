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
import com.crm.common.Tool;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Contract;
import com.crm.models.Supplier;
import com.crm.payload.request.ContractRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContractRepository;
import com.crm.services.ContractService;
import com.crm.specification.builder.ContractSpecificationsBuilder;

@Service
public class ContractServiceImp implements ContractService {

  @Autowired
  private ContractRepository contractRepository;

  @Autowired
  private CombinedRepository combinedRepository;

  @Override
  public Contract createContract(String username, ContractRequest request) {
    Contract contract = new Contract();

    Long combinedId = request.getCombined();
    Combined combined = combinedRepository.findById(combinedId)
        .orElseThrow(() -> new NotFoundException("Combined is not found."));
    contract.setCombined(combined);

    Bid bid = combined.getBid();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();
    if (username.equals(offeree.getUsername())) {
      Integer fines = request.getFinesAgainstContractViolations();
      if (fines > 0) {
        contract.setFinesAgainstContractViolations(fines);
      } else {
        throw new InternalException("Fines against contract violation must be greater than zero.");
      }
    } else {
      throw new NotFoundException("You must be Offeree to create Contract.");
    }

    Boolean required = request.getRequired();
    contract.setRequired(required);

    contractRepository.save(contract);
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
        .orElseThrow(() -> new NotFoundException("Contract is not found."));
    Combined combined = contract.getCombined();

    Bid bid = combined.getBid();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();

    if (username.equals(offeree.getUsername())) {
      String fines = (String) updates.get("finesAgainstContractViolations");
      if (!Tool.isEqual(contract.getFinesAgainstContractViolations(), fines)) {
        contract.setFinesAgainstContractViolations(Integer.valueOf(fines));
      }
      String requiredString = (String) updates.get("required");
      if (!Tool.isEqual(contract.getRequired(), requiredString)) {
        contract.setRequired(Boolean.valueOf(requiredString));
        ;
      }
    } else {
      throw new NotFoundException("You must be Offeree to edit Contract.");
    }

    contractRepository.save(contract);
    return contract;
  }

  @Override
  public void removeContract(Long id, String username) {
    Contract contract = contractRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Contract is not found."));
    Combined combined = contract.getCombined();

    Bid bid = combined.getBid();
    Supplier bidder = bid.getBidder();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();
    if (username.equals(bidder.getUsername()) || username.equals(offeree.getUsername())) {
      contractRepository.deleteById(id);
    } else {
      throw new NotFoundException("You must be Offeree or Biddier to create Contract.");
    }

  }

}
