package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.ShippingLine;
import com.crm.models.dto.ShippingLineDto;
import com.crm.models.mapper.ShippingLineMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingLineRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ShippingLineService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/shipping-line")
public class ShippingLineController {

  private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

  @Autowired
  private ShippingLineService shippingLineService;

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> createShippingLine(@Valid @RequestBody ShippingLineRequest request) {
    ShippingLine shippingLine = shippingLineService.createShippingLine(request);
    ShippingLineDto shippingLineDto = ShippingLineMapper.toShippingLineDto(shippingLine);

    // Set default response body
    DefaultResponse<ShippingLineDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_SHIPPING_LINE_SUCCESSFULLY);
    defaultResponse.setData(shippingLineDto);

    logger.info("createShippingLine with code: {}", shippingLine.getCompanyCode());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @GetMapping("")
  public ResponseEntity<?> getShippingLines(@Valid PaginationRequest request) {

    Page<ShippingLine> pages = shippingLineService.getShippingLines(request);

    PaginationResponse<ShippingLineDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ShippingLine> shippingLines = pages.getContent();
    List<ShippingLineDto> shippingLinesDto = new ArrayList<>();
    shippingLines.forEach(shippingLine -> shippingLinesDto.add(ShippingLineMapper.toShippingLineDto(shippingLine)));
    response.setContents(shippingLinesDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getShippingLine(@PathVariable Long id) {
    ShippingLine shippingLine = shippingLineService.getShippingLine(id);
    ShippingLineDto shippingLineDto = ShippingLineMapper.toShippingLineDto(shippingLine);
    return ResponseEntity.ok(shippingLineDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MODERATOR')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editShippingLine(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    ShippingLine shippingLine = shippingLineService.editShippingLine(id, updates);
    ShippingLineDto shippingLineDto = ShippingLineMapper.toShippingLineDto(shippingLine);

    // Set default response body
    DefaultResponse<ShippingLineDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_SHIPPING_LINE_SUCCESSFULLY);
    defaultResponse.setData(shippingLineDto);

    logger.info("editShippingLine from id: {} with request", id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('MODERATOR')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteShippingLine(@PathVariable Long id) {
    shippingLineService.removeShippingLine(id);

    // Set default response body
    DefaultResponse<ShippingLineDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_SHIPPING_LINE_SUCCESSFULLY);

    logger.info("deleteShippingLine with id: {}", id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
