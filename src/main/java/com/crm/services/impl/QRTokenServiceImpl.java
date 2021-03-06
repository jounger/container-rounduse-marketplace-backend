package com.crm.services.impl;

import java.time.LocalDateTime;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumShippingStatus;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.ForbiddenException;
import com.crm.exception.NotFoundException;
import com.crm.models.Container;
import com.crm.models.Merchant;
import com.crm.models.QRToken;
import com.crm.models.ShippingInfo;
import com.crm.models.ShippingLine;
import com.crm.repository.ContainerRepository;
import com.crm.repository.DriverRepository;
import com.crm.repository.QRTokenRepository;
import com.crm.repository.ShippingInfoRepository;
import com.crm.services.QRTokenService;

@Service
public class QRTokenServiceImpl implements QRTokenService {

  @Autowired
  QRTokenRepository qrTokenRepository;

  @Autowired
  ShippingInfoRepository shippingInfoRepository;

  @Autowired
  DriverRepository driverRepository;

  @Autowired
  ContainerRepository containerRepository;

  @Override
  public QRToken createQRToken(String username, Long id) {
    ShippingInfo shippingInfo = shippingInfoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.SHIPPING_INFO_NOT_FOUND));
    Merchant merchant = shippingInfo.getOutbound().getMerchant();

    QRToken qrToken = new QRToken();

    ShippingLine shippingLine = shippingInfo.getOutbound().getShippingLine();
    qrToken.setShippingInfo(shippingInfo);
    String rawToken = "";
    if (username.equals(merchant.getUsername())) {
      rawToken += EnumShippingStatus.SHIPPING.name();
    } else if (username.equals(shippingLine.getUsername())) {
      rawToken += EnumShippingStatus.DELIVERED.name();
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    rawToken += Tool.randomString();
    // encode data using BASE64
    String token = DatatypeConverter.printBase64Binary(rawToken.getBytes());
    qrToken.setToken(token);

    qrToken.setExpiredDate(LocalDateTime.now().plusMinutes(Constant.QR_CODE_EXPIRED_MINUTES));
    qrTokenRepository.save(qrToken);

    return qrToken;
  }

  @Override
  public Boolean isValidQRTolken(String token) {
    QRToken resetToken = qrTokenRepository.findByToken(token)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.QRTOKEN_NOT_FOUND));
    if (resetToken.getExpiredDate().isAfter(LocalDateTime.now())) {
      return true;
    } else {
      qrTokenRepository.delete(resetToken);
      return false;
    }
  }

  @Override
  public ShippingInfo editShippingInfoByToken(String username, String token) {
    QRToken qrToken = qrTokenRepository.findByToken(token)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.QRTOKEN_NOT_FOUND));
    ShippingInfo shippingInfo = qrToken.getShippingInfo();
    Container container = shippingInfo.getContainer();
    if (!shippingInfo.getContainer().getDriver().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    if (qrToken.getExpiredDate().isBefore(LocalDateTime.now())) {
      throw new NotFoundException(ErrorMessage.QRTOKEN_EXPIRED);
    }
    String rawToken = new String(DatatypeConverter.parseBase64Binary(qrToken.getToken()));
    if (rawToken.startsWith(EnumShippingStatus.SHIPPING.name())) {
      shippingInfo.setStatus(EnumShippingStatus.SHIPPING.name());
    } else if (rawToken.startsWith(EnumShippingStatus.DELIVERED.name())) {
      shippingInfo.setStatus(EnumShippingStatus.DELIVERED.name());
      container.setStatus(EnumSupplyStatus.DELIVERED.name());
    } else {
      throw new NotFoundException(ErrorMessage.SHIPPING_INFO_STATUS_NOT_FOUND);
    }

    containerRepository.save(container);

    shippingInfoRepository.save(shippingInfo);

    qrTokenRepository.delete(qrToken);

    return shippingInfo;
  }

}
