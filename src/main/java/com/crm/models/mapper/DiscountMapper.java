package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Discount;
import com.crm.models.dto.DiscountDto;

public class DiscountMapper {
  public static DiscountDto toDiscountDto(Discount discount) {

    DiscountDto discountDto = new DiscountDto();
    discountDto.setId(discount.getId());
    discountDto.setCode(discount.getCode());
    discountDto.setDetail(discount.getDetail());
    discountDto.setCurrency(discount.getCurrency());
    discountDto.setPercent(discount.getPercent());
    discountDto.setMaximumDiscount(discount.getMaximumDiscount());
    if (discount.getExpiredDate() != null) {
      String expiredDate = Tool.convertLocalDateTimeToString(discount.getExpiredDate());
      discountDto.setExpiredDate(expiredDate);
    }
    return discountDto;

  }
}
