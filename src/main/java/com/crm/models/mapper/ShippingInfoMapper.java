package com.crm.models.mapper;

import com.crm.models.Container;
import com.crm.models.Outbound;
import com.crm.models.ShippingInfo;
import com.crm.models.dto.ShippingInfoDto;

public class ShippingInfoMapper {

  public static ShippingInfoDto toShippingInfoDto(ShippingInfo shippingInfo) {
    ShippingInfoDto shippingInfoDto = new ShippingInfoDto();
    shippingInfoDto.setId(shippingInfo.getId());

    Outbound outbound = shippingInfo.getOutbound();
    shippingInfoDto.setOutbound(OutboundMapper.toOutboundDto(outbound));

    Container container = shippingInfo.getContainer();
    shippingInfoDto.setContainer(ContainerMapper.toContainerDto(container));

    shippingInfoDto.setStatus(shippingInfo.getStatus());

    return shippingInfoDto;
  }
}
