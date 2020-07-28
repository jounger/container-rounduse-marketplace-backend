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
import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Contract;
import com.crm.models.Evidence;
import com.crm.models.Supplier;
import com.crm.models.User;
import com.crm.payload.request.EvidenceRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ContractRepository;
import com.crm.repository.EvidenceRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;
import com.crm.services.EvidenceService;
import com.crm.specification.builder.EvidenceSpecificationsBuilder;

@Service
public class EvidenceServiceImpl implements EvidenceService {

  @Autowired
  private EvidenceRepository evidenceRepository;

  @Autowired
  private ContractRepository contractRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public Evidence createEvidence(Long id, String username, EvidenceRequest request) {
    Evidence evidence = new Evidence();

    Contract contract = contractRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.COMBINED_NOT_FOUND));
    evidence.setContract(contract);

    Combined combined = contract.getCombined();
    Bid bid = combined.getBid();
    Supplier bidder = bid.getBidder();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();
    if (username.equals(bidder.getUsername()) || username.equals(offeree.getUsername())) {
      Supplier supplier = supplierRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(ErrorConstant.SENDER_NOT_FOUND));
      evidence.setSender(supplier);
      String evidenceString = request.getEvidence();
      if (!Tool.isBlank(evidenceString)) {
        evidence.setEvidence(evidenceString);
      } else {
        throw new InternalException(ErrorConstant.EVIDENCE_INVALID);
      }
      evidence.setIsValid(false);
    } else {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }

    Evidence _evidence = evidenceRepository.save(evidence);
    return _evidence;
  }

  @Override
  public Page<Evidence> getEvidencesByUser(String username, PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Evidence> evidences = evidenceRepository.findByUser(username, page);
    return evidences;
  }

  @Override
  public Page<Evidence> getEvidencesByContract(Long id, String username, PaginationRequest request) {
    if (!contractRepository.existsById(id)) {
      throw new NotFoundException(ErrorConstant.CONTRACT_NOT_FOUND);
    }
    Page<Evidence> evidences = null;
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(ErrorConstant.USER_NOT_FOUND));
    String role = user.getRoles().iterator().next().getName();

    if (role.equalsIgnoreCase("ROLE_MODERATOR")) {
      evidences = evidenceRepository.findByContract(id, page);
    } else {
      evidences = evidenceRepository.findByContract(id, username, page);
    }
    return evidences;
  }

  @Override
  public Page<Evidence> searchEvidences(PaginationRequest request, String search) {
    // Extract data from search string
    EvidenceSpecificationsBuilder builder = new EvidenceSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Evidence> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Evidence> pages = evidenceRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public Evidence editEvidence(Long id, String username, Map<String, Object> updates) {
    Evidence evidence = evidenceRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.EVIDENCE_NOT_FOUND));
    Contract contract = evidence.getContract();
    Combined combined = contract.getCombined();
    Bid bid = combined.getBid();
    Supplier bidder = bid.getBidder();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();

    if (!username.equals(bidder.getUsername()) && !username.equals(offeree.getUsername())) {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }
    String evidenceString = String.valueOf(updates.get("evidence"));
    if (updates.get("evidence") != null && !Tool.isBlank(evidenceString)) {
      evidence.setEvidence(evidenceString);
    }
    
    String isValid = String.valueOf(updates.get("isValid"));
    if (updates.get("isValid") != null && !Tool.isEqual(evidence.getIsValid(), isValid)) {
      evidence.setIsValid(Boolean.valueOf(isValid));
    }

    Evidence _evidence = evidenceRepository.save(evidence);
    return _evidence;

  }

  @Override
  public void removeEvidence(Long id, String username) {
    Evidence evidence = evidenceRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.EVIDENCE_NOT_FOUND));
    Contract contract = evidence.getContract();
    Combined combined = contract.getCombined();

    Bid bid = combined.getBid();
    Supplier bidder = bid.getBidder();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();
    if (username.equals(bidder.getUsername()) || username.equals(offeree.getUsername())) {
      evidenceRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }

  }
}
