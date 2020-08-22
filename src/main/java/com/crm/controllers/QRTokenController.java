package com.crm.controllers;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.QRToken;
import com.crm.models.ShippingInfo;
import com.crm.models.dto.ShippingInfoDto;
import com.crm.models.dto.TokenDto;
import com.crm.models.dto.UserDto;
import com.crm.models.mapper.ShippingInfoMapper;
import com.crm.models.mapper.TokenMapper;
import com.crm.payload.request.QRTokenRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.services.QRTokenService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/qr-token")
public class QRTokenController {

  private static final Logger logger = LoggerFactory.getLogger(QRTokenController.class);

  @Autowired
  QRTokenService qrTokenService;

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('SHIPPINGLINE')")
  @PostMapping("/shipping-info/{id}")
  public ResponseEntity<?> createQRToken(@PathVariable("id") Long id) throws MessagingException, IOException {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    QRToken qrToken = qrTokenService.createQRToken(username, id);
    DefaultResponse<TokenDto> response = new DefaultResponse<TokenDto>();
    response.setMessage(SuccessMessage.QR_TOKEN_GENERATE_SUCCESSFULLY);
    response.setData(TokenMapper.toTokenDto(qrToken));
    logger.info("{} createQRToken with shipping info id: {} and token: {}", username, id, qrToken.getToken());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("")
  public ResponseEntity<?> isValidQRToken(@Valid @RequestBody QRTokenRequest request) {
    Boolean isValidQRToken = qrTokenService.isValidQRTolken(request.getToken());
    DefaultResponse<Boolean> response = new DefaultResponse<Boolean>();
    response.setData(isValidQRToken);
    logger.info("QR Token: {}", request.getToken());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Transactional
  @PreAuthorize("hasRole('DRIVER')")
  @PatchMapping("")
  public ResponseEntity<?> editShippingInfoByToken(HttpServletRequest httpRequest) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    String token = httpRequest.getHeader("Authentication");
    ShippingInfo shippingInfo = qrTokenService.editShippingInfoByToken(username, token);
    DefaultResponse<ShippingInfoDto> response = new DefaultResponse<ShippingInfoDto>();
    response.setMessage(SuccessMessage.EDIT_SHIPPING_INFO_STATUS_SUCCESSFULLY);
    response.setData(ShippingInfoMapper.toShippingInfoDto(shippingInfo));
    logger.info("{} editShippingInfoByToken by Token: {}", token);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
