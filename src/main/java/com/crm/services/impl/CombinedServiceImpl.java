package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumCombinedStatus;
import com.crm.exception.NotFoundException;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.MerchantRepository;
import com.crm.services.CombinedService;

@Service
public class CombinedServiceImpl implements CombinedService {

  @Autowired
  private CombinedRepository combinedRepository;

  @Autowired
  private BiddingDocumentRepository biddingDocumentRepository;

  @Autowired
  private MerchantRepository merchantRepository;

  @Autowired
  private ForwarderRepository forwarderRepository;

  @Override
  public Combined createCombined(CombinedRequest request) {
    Combined combined = new Combined();

    BiddingDocument biddingDocument = biddingDocumentRepository.findById(request.getBiddingDocument())
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));
    combined.setBiddingDocument(biddingDocument);

    combined.setStatus(EnumCombinedStatus.INFO_RECEIVED.name());

    combinedRepository.save(combined);
    return combined;
  }

  @Override
  public Combined getCombined(Long id) {
    Combined combined = combinedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Combined is not found."));
    return combined;
  }

  @Override
  public Page<Combined> getCombinedsByMerchant(Long id, PaginationRequest request) {
    if (merchantRepository.existsById(id)) {
      Page<Combined> combineds = combinedRepository.findByMerchant(id,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
      return combineds;
    }else {
      throw new NotFoundException("Merchant is not found.");
    }
  }

  @Override
  public Page<Combined> getCombinedsByForwarder(String username, PaginationRequest request) {
    if(forwarderRepository.existsByUsername(username)) {
      Page<Combined> combineds = combinedRepository.findByForwarder(username,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt")));
      return combineds;
    }else {
      throw new NotFoundException("Forwarder is not found.");
    } 
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

    BiddingDocument biddingDocument = biddingDocumentRepository.findById(request.getBiddingDocument())
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));
    combined.setBiddingDocument(biddingDocument);

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
  public Combined editCombined(Long id, Map<String, Object> updates) {
    Combined combined = combinedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Combined is not found."));

    String statusString = (String) updates.get("status");
    if (statusString != null && !statusString.isEmpty()) {
      EnumCombinedStatus status = EnumCombinedStatus.findByName(statusString);
      if (status != null) {
        combined.setStatus(status.name());
      } else {
        throw new NotFoundException("Status is not found.");
      }
    }

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