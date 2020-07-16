package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumCurrency;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.Discount;
import com.crm.payload.request.DiscountRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.DiscountRepository;
import com.crm.services.DiscountService;

@Service
public class DiscountServiceImpl implements DiscountService {

  @Autowired
  private DiscountRepository discountRepository;

  @Override
  public Page<Discount> getDiscounts(PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Discount> pages = discountRepository.findAll(pageRequest);
    return pages;
  }

  @Override
  public Discount getDiscountById(Long id) {
    Discount discount = discountRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Discount is not found."));
    return discount;
  }

  @Override
  public Discount getDiscountByCode(String code) {
    Discount discount = discountRepository.findByCode(code)
        .orElseThrow(() -> new NotFoundException("ERROR: Discount is not found."));
    return discount;
  }

  @Override
  public Discount createDiscount(DiscountRequest request) {
    if (discountRepository.existsByCode(request.getCode())) {
      throw new DuplicateRecordException("ERROR: Discount already exists.");
    }
    Discount discount = new Discount();
    discount.setCode(request.getCode());
    discount.setDetail(request.getDetail());
    if (request.getCurrency() != null && !request.getCurrency().isEmpty()) {
      discount.setCurrency(EnumCurrency.findByName(request.getCurrency()).name());
    } else {
      throw new NotFoundException("ERROR: Currency is not found.");
    }
    discount.setPercent(request.getPercent());
    discount.setMaximumDiscount(request.getMaximumDiscount());
    if (request.getExpiredDate() != null && !request.getExpiredDate().isEmpty()) {
      LocalDateTime expiredDate = Tool.convertToLocalDateTime(request.getExpiredDate());
      discount.setExpiredDate(expiredDate);
    }
    discountRepository.save(discount);
    return discount;
  }

  @Override
  public Discount updateDiscount(DiscountRequest request) {
    Discount discount = discountRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Discount is not found."));

    if (discountRepository.existsByCode(request.getCode())) {
      if (request.getCode().equals(discount.getCode())) {
      } else {
        throw new DuplicateRecordException("ERROR: Discount already exists.");
      }
    }
    discount.setCode(request.getCode());

    discount.setDetail(request.getDetail());
    if (request.getCurrency() != null && !request.getCurrency().isEmpty()) {
      discount.setCurrency(EnumCurrency.findByName(request.getCurrency()).name());
    } else {
      throw new NotFoundException("ERROR: Currency is not found.");
    }
    discount.setPercent(request.getPercent());
    discount.setMaximumDiscount(request.getMaximumDiscount());
    if (request.getExpiredDate() != null && !request.getExpiredDate().isEmpty()) {
      LocalDateTime expiredDate = Tool.convertToLocalDateTime(request.getExpiredDate());
      discount.setExpiredDate(expiredDate);
    }
    discountRepository.save(discount);
    return discount;
  }

  @Override
  public Discount editDiscount(Map<String, Object> updates, Long id) {
    Discount discount = discountRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Discount is not found."));

    String code = (String) updates.get("code");
    if (code != null && !code.isEmpty()) {
      if (discountRepository.existsByCode(code)) {
        if (code.equals(discount.getCode())) {
        } else {
          throw new DuplicateRecordException("ERROR: Discount already exists.");
        }
      }
      discount.setCode(code);
    }

    String detail = (String) updates.get("detail");
    if (!Tool.isEqual(discount.getDetail(), detail)) {
      discount.setDetail(detail);
    }

    String currency = (String) updates.get("currency");
    if (!Tool.isEqual(discount.getCurrency(), currency)) {
      String currencyName = EnumCurrency.findByName(currency).name();
      if (currencyName != null && !currencyName.isEmpty()) {
        discount.setCurrency(currencyName);
      } else {
        throw new NotFoundException("ERROR: Currency is not found.");
      }
    }

    String percent = (String) updates.get("percent");
    if (!Tool.isEqual(discount.getPercent(), percent)) {
      discount.setPercent(Double.valueOf(percent));
    }

    String maximumDiscount = (String) updates.get("maximumDiscount");
    if (!Tool.isEqual(discount.getMaximumDiscount(), maximumDiscount)) {
      discount.setMaximumDiscount(Double.valueOf(maximumDiscount));
    }

    String expiredDateString = (String) updates.get("expiredDate");
    if (!Tool.isEqual(discount.getExpiredDate().toString(), expiredDateString)) {
      discount.setExpiredDate(Tool.convertToLocalDateTime(expiredDateString));
    }

    discountRepository.save(discount);
    return discount;
  }

  @Override
  public void removeDiscount(Long id) {
    if (discountRepository.existsById(id)) {
      discountRepository.deleteById(id);
    } else {
      new NotFoundException("ERROR: Discount is not found.");
    }
  }

}
