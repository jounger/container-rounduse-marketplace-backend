package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Operator;
import com.crm.payload.request.OperatorRequest;
import com.crm.payload.request.PaginationRequest;

public interface OperatorService {

  Page<Operator> getOperators(PaginationRequest request);

  Operator getOperatorById(Long id);

  void createOperator(OperatorRequest request);
  
  Operator updateOperator(OperatorRequest request);
  
  void removeOperator(Long id);
}
