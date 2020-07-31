package com.crm.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumShippingStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.Contract;
import com.crm.models.Outbound;
import com.crm.models.ShippingInfo;
import com.crm.models.Supplier;
import com.crm.models.User;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.ContractRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingInfoRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.UserRepository;
import com.crm.services.BidService;
import com.crm.services.CombinedService;
import com.crm.services.ShippingInfoService;

@Service
public class CombinedServiceImpl implements CombinedService {

  @Autowired
  private CombinedRepository combinedRepository;

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private BidService bidService;

  @Autowired
  private ShippingInfoService shippingInfoService;

  @Override
  public Combined createCombined(Long bidId, String username, CombinedRequest request) {
    Combined combined = new Combined();

    Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new NotFoundException(ErrorConstant.BID_NOT_FOUND));
    if (bid.getCombined() != null) {
      throw new DuplicateRecordException(ErrorConstant.BID_INVALID_CREATE);
    }
    Map<String, Object> updates = new HashMap<>();
    updates.put("status", EnumBidStatus.ACCEPTED.name());
    if (request.getContainers() != null) {
      updates.put("combinedContainers", request.getContainers());
    }

    bid = bidService.editBid(bidId, username, updates);
    combined.setBid(bid);

    combined.setIsCanceled(false);
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

    Outbound outbound = biddingDocument.getOutbound();
    List<String> containers = request.getContainers();
    ShippingInfoRequest shippingInfoRequest = new ShippingInfoRequest();
    shippingInfoRequest.setCombined(combined.getId());
    shippingInfoRequest.setOutbound(outbound.getId());
    shippingInfoRequest.setStatus(EnumShippingStatus.INFO_RECEIVED.name());
    containers.forEach(containerId -> {
      try {
        Container container = containerRepository.findById(Long.parseLong(containerId))
            .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_NOT_FOUND));
        shippingInfoRequest.setContainer(container.getId());
        ShippingInfo shippingInfo = shippingInfoService.createShippingInfo(shippingInfoRequest);
        combined.getShippingInfos().add(shippingInfo);
      } catch (NumberFormatException e) {
        throw new NumberFormatException(ErrorConstant.CONTAINER_NOT_FOUND);
      }

    });

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
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.USER_NOT_FOUND));
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
        .orElseThrow(() -> new NotFoundException(ErrorConstant.COMBINED_NOT_FOUND));

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
      throw new NotFoundException(ErrorConstant.COMBINED_NOT_FOUND);
    }
  }

}
