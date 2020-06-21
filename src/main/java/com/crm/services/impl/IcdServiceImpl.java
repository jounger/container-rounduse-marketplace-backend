package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Icd;
import com.crm.payload.request.IcdRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.IcdRepository;
import com.crm.services.IcdService;

@Service
public class IcdServiceImpl implements IcdService {

  @Autowired
  private IcdRepository icdRepository;

  @Override
  public Page<Icd> getIcds(PaginationRequest request) {
    Page<Icd> pages = icdRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public Icd getIcdById(Long id) {
    Icd icd = icdRepository.findById(id).orElseThrow(() -> new NotFoundException("ERROR: Icd is not found."));
    return icd;
  }

  @Override
  public void createIcd(IcdRequest request) {
    Icd icd = new Icd();
    icd.setFullname(request.getFullname());
    String nameCode = request.getNameCode();
    if (icdRepository.existsByNameCode(nameCode)) {
      throw new DuplicateRecordException("ICD name code already existed.");
    }
    icd.setNameCode(nameCode);
    icd.setAddress(request.getAddress());

    icdRepository.save(icd);
  }

  @Override
  public Icd updateIcd(IcdRequest request) {
    Icd icd = icdRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Icd is not found."));

    icd.setFullname(request.getFullname());
    icd.setNameCode(request.getNameCode());
    icd.setAddress(request.getAddress());

    icdRepository.save(icd);

    return (icd);
  }

  @Override
  public void deleteIcd(Long id) {
    if (icdRepository.existsById(id)) {
      icdRepository.deleteById(id);
    } else {
      throw new NotFoundException("ERROR: Icd is not found.");
    }
  }

}
