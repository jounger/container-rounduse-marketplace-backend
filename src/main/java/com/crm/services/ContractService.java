package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Contract;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ContractRequest;

public interface ContractService {

  Contract createContract(String username, ContractRequest request);
  
  Page<Contract> getContractsByUser(String username, PaginationRequest request);

  Page<Contract> searchContracts(PaginationRequest request, String search);

  Contract editContract(Long id, String username, Map<String, Object> updates);

  void removeContract(Long id, String username);
}
