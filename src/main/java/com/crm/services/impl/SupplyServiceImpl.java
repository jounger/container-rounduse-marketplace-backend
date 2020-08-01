package com.crm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.repository.SupplyRepository;
import com.crm.services.SupplyService;

@Service
public class SupplyServiceImpl implements SupplyService{

  @Autowired
  private SupplyRepository supplyRepository;
  
  @Override
  public Boolean existsByCode(String code) {
    Boolean existsCode = supplyRepository.existsByCode(code);
    return existsCode;
  }

}
