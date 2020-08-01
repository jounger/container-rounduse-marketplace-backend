package com.crm.models.mapper;

import com.crm.models.Container;
import com.crm.models.Outbound;
import com.crm.models.ShippingInfo;
import com.crm.models.dto.ShippingInfoDto;

public class ShippingInfoMapper {

  public static ShippingInfoDto toShippingInfoDto(ShippingInfo shippingInfo) {
    ShippingInfoDto shippingInfoDto = new ShippingInfoDto();
    shippingInfo.setId(shippingInfo.getId());

    Outbound outbound = shippingInfo.getOutbound();
    shippingInfoDto.setSupplyCode(outbound.getCode());

    Container container = shippingInfo.getContainer();
    shippingInfoDto.setContainerNumber(container.getContainerNumber());

    shippingInfoDto.setStatus(shippingInfo.getStatus());

    return shippingInfoDto;
  }
}
