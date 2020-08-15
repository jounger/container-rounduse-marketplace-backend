package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.Contract;
import com.crm.models.Supplier;
import com.crm.models.User;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.ContractRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.CombinedRepository;
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

  @Override
  public Combined createCombined(Long bidId, String username, CombinedRequest request) {
    Combined combined = new Combined();

    Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new NotFoundException(ErrorMessage.BID_NOT_FOUND));
    if (bid.getCombined() != null) {
      throw new DuplicateRecordException(ErrorMessage.BID_INVALID_CREATE);
    }
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    if(biddingDocument.getBidClosing().isBefore(LocalDateTime.now())) {
      throw new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_TIME_OUT);
    }
    List<Long> containersId = new ArrayList<>();
    List<Container> containers = new ArrayList<Container>(bid.getContainers());
    if (!biddingDocument.getIsMultipleAward()) {
      for (int i = 0; i < containers.size(); i++) {
        Container container = containers.get(i);
        containersId.add(container.getId());
      }
    }else {
      containersId = request.getContainers();
    }

    if (request.getContainers() == null || request.getContainers().size() == 0) {
      throw new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND);
    }

    bid = bidService.editBidWhenCombined(bidId, username, containersId);
    combined.setBid(bid);

    combined.setIsCanceled(false);
    bid = combined.getBid();
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
        throw new InternalException(ErrorMessage.CONTRACT_INVALID_FINES);
      }
    } else if (!username.equals(offeree.getUsername())) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
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
      combined.setIsCanceled(Boolean.valueOf(isCanceled));
      Map<String, Object> updatesBid = new HashMap<>();
      updatesBid.put("status", EnumBidStatus.REJECTED.name());
      bidService.editBid(bid.getId(), username, updatesBid);
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
