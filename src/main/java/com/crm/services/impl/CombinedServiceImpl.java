package com.crm.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumCombinedStatus;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Contract;
import com.crm.models.Outbound;
import com.crm.models.Supplier;
import com.crm.models.User;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.ContractRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.UserRepository;
import com.crm.services.BidService;
import com.crm.services.CombinedService;

@Service
public class CombinedServiceImpl implements CombinedService {

  @Autowired
  private CombinedRepository combinedRepository;

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BidService bidService;

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private OutboundRepository outboundRepository;

  @Override
  public Combined createCombined(Long bidId, String username, CombinedRequest request) {
    Combined combined = new Combined();

    Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new NotFoundException(ErrorConstant.BID_NOT_FOUND));
    if(bid.getCombined() != null) {
      throw new DuplicateRecordException(ErrorConstant.BID_INVALID_CREATE);
    }
    Map<String, Object> updates = new HashMap<>();
    updates.put("status", EnumBidStatus.ACCEPTED.name());
    if(request.getContainers() != null) {
      updates.put("combinedContainers", request.getContainers());
    }
    
    bid = bidService.editBid(bidId, username, updates);
    combined.setBid(bid);

    combined.setStatus(EnumCombinedStatus.INFO_RECEIVED.name());
    bid = combined.getBid();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Supplier offeree = biddingDocument.getOfferee();
    ContractRequest contracRequest = request.getContract();
    Contract contract = new Contract();
    Boolean required = contracRequest.getRequired();
    contract.setRequired(required);
    contract.setFinesAgainstContractViolations(0D);
    if (username.equals(offeree.getUsername()) && required == true) {
      Double fines = contracRequest.getFinesAgainstContractViolations();
      if (fines > 0) {
        contract.setFinesAgainstContractViolations(fines);
      } else {
        throw new InternalException(ErrorConstant.CONTRACT_INVALID_FINES);
      }
    } else if (!username.equals(offeree.getUsername())) {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }

    combined.setContract(contract);
    contract.setCombined(combined);

    Combined _combined = combinedRepository.save(combined);

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
        .orElseThrow(() -> new NotFoundException(ErrorConstant.COMBINED_NOT_FOUND));
    return combined;
  }

  @Override
  public Page<Combined> getCombinedsByUser(String username, PaginationRequest request) {
    User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(ErrorConstant.USER_NOT_FOUND));
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
  public Combined updateCombined(CombinedRequest request) {
    Combined combined = combinedRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.COMBINED_NOT_FOUND));

    Bid bid = bidRepository.findById(request.getBid())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BID_NOT_FOUND));
    combined.setBid(bid);

    EnumCombinedStatus status = EnumCombinedStatus.findByName(request.getStatus());
    if (status != null) {
      combined.setStatus(status.name());
    } else {
      throw new NotFoundException(ErrorConstant.BID_STATUS_NOT_FOUND);
    }

    combinedRepository.save(combined);
    return combined;
  }

  @Override
  public Combined editCombined(Long id, String username, Map<String, Object> updates) {
    Combined combined = combinedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.COMBINED_NOT_FOUND));

    Bid bid = combined.getBid();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    String statusString = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isBlank(statusString)) {
      EnumCombinedStatus status = EnumCombinedStatus.findByName(statusString);
      if (status == null) {
        throw new NotFoundException(ErrorConstant.BID_STATUS_NOT_FOUND);
      }
      combined.setStatus(status.name());
      if (status.equals(EnumCombinedStatus.CANCELED)) {
        Map<String, Object> updatesBid = new HashMap<>();
        updatesBid.put("status", EnumBidStatus.REJECTED.name());
        bidService.editBid(bid.getId(), username, updatesBid);
      }

      if (status.equals(EnumCombinedStatus.DELIVERED)) {
        bid.getContainers().parallelStream().forEach(container -> {
          container.setStatus(EnumSupplyStatus.DELIVERED.name());
          containerRepository.save(container);
        });
        if (bidRepository.isAllCombinedByBiddingDocument(biddingDocument.getId())) {
          Outbound outbound = biddingDocument.getOutbound();
          outbound.setStatus(EnumSupplyStatus.DELIVERED.name());
          outboundRepository.save(outbound);
        }
      }
    }
    Combined _combined = combinedRepository.save(combined);
    return _combined;
  }

  @Override
  public void removeCombined(Long id) {
    if (combinedRepository.existsById(id)) {
      combinedRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorConstant.COMBINED_NOT_FOUND);
    }
  }

}
