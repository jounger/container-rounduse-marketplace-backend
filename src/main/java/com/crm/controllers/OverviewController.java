package com.crm.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.ErrorMessage;
import com.crm.exception.ForbiddenException;
import com.crm.payload.request.OverviewRequest;
import com.crm.payload.response.ForwarderOverviewResponse;
import com.crm.payload.response.MerchantOverviewResponse;
import com.crm.payload.response.OperatorOverviewResponse;
import com.crm.payload.response.ShippingLineOverviewResponse;
import com.crm.services.OverviewService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/overview")
public class OverviewController {

  @Autowired
  private OverviewService overviewService;

  @GetMapping
  public ResponseEntity<?> overview(@Valid @RequestBody OverviewRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    String role = userDetails.getAuthorities().iterator().next().getAuthority();
    if (role.equals("ROLE_MODERATOR")) {
      OperatorOverviewResponse operatorOverviewResponse = overviewService.getOverviewByOperator(request);
      return ResponseEntity.ok(operatorOverviewResponse);
    } else if (role.equals("ROLE_MERCHANT")) {
      MerchantOverviewResponse merchantOverviewResponse = overviewService.getOverviewByMerchant(username, request);
      return ResponseEntity.ok(merchantOverviewResponse);
    } else if (role.equals("ROLE_FORWARDER")) {
      ForwarderOverviewResponse forwarderOverviewResponse = overviewService.getOverviewByForwarder(username, request);
      return ResponseEntity.ok(forwarderOverviewResponse);
    } else if(role.equals("ROLE_SHIPPINGLINE")) {
      ShippingLineOverviewResponse shippingLineOverviewResponse = overviewService.getOverviewByShippingLine(username, request);
      return ResponseEntity.ok(shippingLineOverviewResponse);
    }else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
  }

}
