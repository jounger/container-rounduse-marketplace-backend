package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Operator;
import com.crm.models.dto.OperatorDto;
import com.crm.models.mapper.OperatorMapper;
import com.crm.payload.request.OperatorRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.OperatorService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/operator")
public class OperatorController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  private OperatorService operatorService;

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getOperator(@PathVariable Long id) {
    Operator operator = operatorService.getOperatorById(id);
    OperatorDto operatorDto = new OperatorDto();
    operatorDto = OperatorMapper.toOperatorDto(operator);
    return ResponseEntity.ok(operatorDto);
  }

  @GetMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getOperators(@Valid PaginationRequest request) {

    Page<Operator> pages = operatorService.getOperators(request);
    PaginationResponse<OperatorDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Operator> operators = pages.getContent();
    List<OperatorDto> operatorDto = new ArrayList<>();
    operators.forEach(operator -> operatorDto.add(OperatorMapper.toOperatorDto(operator)));
    response.setContents(operatorDto);

    return ResponseEntity.ok(response);

  }

  @PostMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createOperator(@Valid @RequestBody OperatorRequest request) {
    operatorService.createOperator(request);
    logger.info("Operator {} created.", request.getUsername());
    return ResponseEntity.ok(new MessageResponse("Operator created successfully"));
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateOperator(@Valid @RequestBody OperatorRequest request){
    Operator operator = operatorService.updateOperator(request);
    OperatorDto operatorDto = OperatorMapper.toOperatorDto(operator);
    return ResponseEntity.ok(operatorDto);
  }
  
  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> removeOperator(@PathVariable Long id){       
    operatorService.removeOperator(id);
    return ResponseEntity.ok(new MessageResponse("Operator has remove successfully"));
  }
}
