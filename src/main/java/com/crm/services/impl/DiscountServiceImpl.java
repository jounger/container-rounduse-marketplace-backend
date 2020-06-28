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
  public Discount createDiscount(DiscountRequest request) {
    if (discountRepository.existsByCode(request.getCode())) {
      throw new DuplicateRecordException("ERROR: Discount already exists.");
    }
    Discount discount = new Discount();
    discount.setCode(request.getCode());
    discount.setDetail(request.getDetail());
    if (request.getCurrency() != null) {
      discount.setCurrency(EnumCurrency.findByName(request.getCurrency()).name());
    } else {
      throw new NotFoundException("ERROR: Currency is not found.");
    }
    discount.setPercent(request.getPercent());
    discount.setMaximumDiscount(request.getMaximumDiscount());
    if (request.getExpiredDate() != null) {
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
    discount.setCode(request.getCode());
    discount.setDetail(request.getDetail());
    if (request.getCurrency() != null) {
      discount.setCurrency(EnumCurrency.findByName(request.getCurrency()).name());
    } else {
      throw new NotFoundException("ERROR: Currency is not found.");
    }
    discount.setPercent(request.getPercent());
    discount.setMaximumDiscount(request.getMaximumDiscount());
    if (request.getExpiredDate() != null) {
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
    if (code != null) {
      discount.setCode(code);
    }
    String detail = (String) updates.get("detail");
    if (detail != null) {
      discount.setDetail(detail);
    }

    String currency = (String) updates.get("currency");
    if (currency != null) {
      String currencyName = EnumCurrency.findByName(currency).name();
      if (currencyName != null) {
        discount.setCurrency(currencyName);
      } else {
        throw new NotFoundException("ERROR: Currency is not found.");
      }
    }

    Double percent = (Double) updates.get("percent");
    if (percent != null) {
      discount.setPercent(percent);
    }

    Double maximumDiscount = (Double) updates.get("maximumDiscount");
    if (maximumDiscount != null) {
      discount.setMaximumDiscount(maximumDiscount);
    }
    String expiredDateString = (String) updates.get("expiredDate");
    if (expiredDateString != null) {
      LocalDateTime expiredDate = Tool.convertToLocalDateTime(expiredDateString);
      discount.setExpiredDate(expiredDate);
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
