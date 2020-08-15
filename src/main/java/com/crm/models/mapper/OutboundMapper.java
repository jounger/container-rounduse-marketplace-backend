package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Outbound;
import com.crm.models.dto.BookingDto;
import com.crm.models.dto.OutboundDto;

public class OutboundMapper {

  public static OutboundDto toOutboundDto(Outbound outbound) {
    if (outbound == null) {
      return null;
    }

    OutboundDto dto = new OutboundDto();

    dto.setId(outbound.getId());
    dto.setCode(outbound.getCode());
    dto.setShippingLine(ShippingLineMapper.toShippingLineDto(outbound.getShippingLine()));

    dto.setContainerType(ContainerTypeMapper.toContainerTypeDto(outbound.getContainerType()));
    dto.setStatus(outbound.getStatus());

    BookingDto bookingDto = BookingMapper.toBookingDto(outbound.getBooking());
    dto.setBooking(bookingDto);

    dto.setGoodsDescription(outbound.getGoodsDescription());

    if (outbound.getPackingTime() != null) {
      String packingTime = Tool.convertLocalDateTimeToString(outbound.getPackingTime());
      dto.setPackingTime(packingTime);
    }

    if (outbound.getDeliveryTime() != null) {
      String deliveryTime = Tool.convertLocalDateTimeToString(outbound.getDeliveryTime());
      dto.setDeliveryTime(deliveryTime);
    }

    dto.setPackingStation(outbound.getPackingStation());
    dto.setGrossWeight(outbound.getGrossWeight());
    dto.setUnitOfMeasurement(outbound.getUnitOfMeasurement());

    return dto;

  }
}
