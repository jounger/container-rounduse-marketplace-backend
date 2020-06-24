package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Operator;
import com.crm.payload.request.OperatorRequest;
import com.crm.payload.request.PaginationRequest;

public interface OperatorService {

  void createOperator(OperatorRequest request);

  Operator getOperatorById(Long id);

  Page<Operator> getOperators(PaginationRequest request);

  Operator updateOperator(OperatorRequest request);

  Operator editOperator(Long id, Map<String, Object> updates);

  void removeOperator(Long id);
}
