package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.ShippingInfo;
import com.crm.models.dto.ShippingInfoDto;
import com.crm.models.mapper.ShippingInfoMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ShippingInfoService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/shipping-info")
public class ShippingInfoController {

  @Autowired
  private ShippingInfoService shippingInfoService;

  @PreAuthorize("hasRole('FORWARDER') or hasRole('DRIVER') or hasRole('MERCHANT')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getShippingInfo(@PathVariable("id") Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    ShippingInfo shippingInfo = shippingInfoService.getShippingInfo(id, username);
    ShippingInfoDto shippingInfoDto = ShippingInfoMapper.toShippingInfoDto(shippingInfo);
    return ResponseEntity.ok(shippingInfoDto);
  }

  @PreAuthorize("hasRole('FORWARDER')")
  @GetMapping("/bid/{id}")
  public ResponseEntity<?> getShippingInfosByBid(@PathVariable("id") Long id, @Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<ShippingInfo> pages = shippingInfoService.getShippingInfosByBid(id, username, request);

    PaginationResponse<ShippingInfoDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ShippingInfo> shippingInfos = pages.getContent();
    List<ShippingInfoDto> shippingInfosDto = new ArrayList<>();
    shippingInfos.forEach(shippingInfo -> shippingInfosDto.add(ShippingInfoMapper.toShippingInfoDto(shippingInfo)));
    response.setContents(shippingInfosDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT') or hasRole('SHIPPINGLINE')")
  @GetMapping("/combined/{id}")
  public ResponseEntity<?> getShippingInfosByCombined(@PathVariable("id") Long id, @Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<ShippingInfo> pages = shippingInfoService.getShippingInfosByCombined(id, username, request);

    PaginationResponse<ShippingInfoDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ShippingInfo> shippingInfos = pages.getContent();
    List<ShippingInfoDto> shippingInfosDto = new ArrayList<>();
    shippingInfos.forEach(shippingInfo -> shippingInfosDto.add(ShippingInfoMapper.toShippingInfoDto(shippingInfo)));
    response.setContents(shippingInfosDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('DRIVER')")
  @GetMapping("/driver")
  public ResponseEntity<?> getShippingInfosByDriver(@Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<ShippingInfo> pages = shippingInfoService.getShippingInfosByDriver(username, request);

    PaginationResponse<ShippingInfoDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ShippingInfo> shippingInfos = pages.getContent();
    List<ShippingInfoDto> shippingInfosDto = new ArrayList<>();
    shippingInfos.forEach(shippingInfo -> shippingInfosDto.add(ShippingInfoMapper.toShippingInfoDto(shippingInfo)));
    response.setContents(shippingInfosDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT')")
  @GetMapping("/outbound/{id}")
  public ResponseEntity<?> getShippingInfosByOutbound(@PathVariable("id") Long id, @Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<ShippingInfo> pages = shippingInfoService.getShippingInfosByOutbound(id, username, request);

    PaginationResponse<ShippingInfoDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ShippingInfo> shippingInfos = pages.getContent();
    List<ShippingInfoDto> shippingInfosDto = new ArrayList<>();
    shippingInfos.forEach(shippingInfo -> shippingInfosDto.add(ShippingInfoMapper.toShippingInfoDto(shippingInfo)));
    response.setContents(shippingInfosDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('DRIVER') or hasRole('FORWARDER')")
  @PatchMapping(value = "/{id}")
  public ResponseEntity<?> editShippingInfo(@PathVariable("id") Long id, @RequestBody String status) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    ShippingInfo shippingInfo = shippingInfoService.editShippingInfo(id, username, status);
    ShippingInfoDto shippingInfoDto = ShippingInfoMapper.toShippingInfoDto(shippingInfo);

    // Set default response body
    DefaultResponse<ShippingInfoDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_SHIPPING_INFO_SUCCESSFULLY);
    defaultResponse.setData(shippingInfoDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteShippingInfo(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    shippingInfoService.removeShippingInfo(id, username);

    // Set default response body
    DefaultResponse<ShippingInfoDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_SHIPPING_INFO_SUCCESSFULLY);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(defaultResponse);
  }

}
