package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Booking;
import com.crm.models.dto.BookingDto;

public class BookingMapper {
  public static BookingDto toBookingDto(Booking booking) {
    if (booking == null) {
      return null;
    }

    BookingDto dto = new BookingDto();

    dto.setId(booking.getId());
    dto.setPortOfLoading(PortMapper.toPortDto(booking.getPortOfLoading()));
    dto.setNumber(booking.getNumber());
    dto.setUnit(booking.getUnit());

    if (booking.getCutOffTime() != null) {
      String cutOffTime = Tool.convertLocalDateTimeToString(booking.getCutOffTime());
      dto.setCutOffTime(cutOffTime);
    }

    dto.setIsFcl(booking.getIsFcl());
    return dto;
  }
}
