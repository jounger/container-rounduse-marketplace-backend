package com.crm.services;

import com.crm.models.QRToken;
import com.crm.models.ShippingInfo;

public interface QRTokenService {

  QRToken createQRToken(String username, Long id);

  Boolean isValidQRTolken(String token);

  ShippingInfo editShippingInfoByToken(String username, String token);
}
