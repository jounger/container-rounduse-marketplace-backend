package com.crm.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumCombinedStatus;
import com.crm.enums.EnumSupplyStatus;
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

    Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new NotFoundException("Bidding document is not found."));
    combined.setBid(bid);
    Map<String, Object> updates = new HashMap<>();
    updates.put("status", EnumBidStatus.ACCEPTED.name());
    bidService.editBid(bidId, username, updates);

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
        throw new InternalException("Fines against contract violation must be greater than zero.");
      }
    } else if (!username.equals(offeree.getUsername())) {
      throw new NotFoundException("You must be Offeree to create Contract.");
    }

    combined.setContract(contract);
    contract.setCombined(combined);

    combinedRepository.save(combined);

    return combined;
  }

  @Override
  public Page<Combined> getCombinedsByBiddingDocument(Long id, Long userId, PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Combined> combines = combinedRepository.findByBiddingDocument(id, userId, page);
    return combines;
  }

  @Override
  public Combined getCombined(Long id) {
    Combined combined = combinedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Combined is not found."));
    return combined;
  }

  @Override
  public Page<Combined> getCombinedsByUser(Long id, PaginationRequest request) {
    User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User is not found."));
    String role = user.getRoles().iterator().next().getName();
    Page<Combined> combineds = null;
    if (role.equalsIgnoreCase("ROLE_MERCHANT")) {
      combineds = combinedRepository.findByMerchant(id,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
    } else if (role.equalsIgnoreCase("ROLE_FORWARDER")) {
      combineds = combinedRepository.findByForwarder(id,
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
        .orElseThrow(() -> new NotFoundException("Combined is not found."));

    Bid bid = bidRepository.findById(request.getBid())
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));
    combined.setBid(bid);

    EnumCombinedStatus status = EnumCombinedStatus.findByName(request.getStatus());
    if (status != null) {
      combined.setStatus(status.name());
    } else {
      throw new NotFoundException("Status is not found.");
    }

    combinedRepository.save(combined);
    return combined;
  }

  @Override
  public Combined editCombined(Long id, String username, Map<String, Object> updates) {
    Combined combined = combinedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Combined is not found."));

    Bid bid = combined.getBid();
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    String statusString = (String) updates.get("status");
    EnumCombinedStatus status = EnumCombinedStatus.findByName(statusString);
    if (!Tool.isBlank(statusString) && status != null) {
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
    } else {
      throw new NotFoundException("Status is not found.");
    }
    combinedRepository.save(combined);
    return combined;
  }

  @Override
  public void removeCombined(Long id) {
    if (combinedRepository.existsById(id)) {
      combinedRepository.deleteById(id);
    } else {
      throw new NotFoundException("Combined is not found.");
    }
  }

}
