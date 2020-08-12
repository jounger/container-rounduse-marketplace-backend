package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumPaymentType;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Contract;
import com.crm.models.Payment;
import com.crm.models.Supplier;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PaymentRequest;
import com.crm.repository.ContractRepository;
import com.crm.repository.PaymentRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;
import com.crm.services.PaymentService;
import com.crm.specification.builder.PaymentSpecificationsBuilder;

@Service
public class PaymentServiceImpl implements PaymentService {

  @Autowired
  private PaymentRepository paymentRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private ContractRepository contractRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public Payment createPayment(Long id, String username, PaymentRequest request) {
    Payment payment = new Payment();

    Supplier sender = supplierRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.SENDER_NOT_FOUND));
    payment.setSender(sender);

    Supplier recipient = supplierRepository.findByUsername(request.getRecipient())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.RECIPIENT_NOT_FOUND));
    payment.setRecipient(recipient);

    Contract contract = contractRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTRACT_NOT_FOUND));
    payment.setContract(contract);

    payment.setDetail(request.getDetail());
    if (request.getAmount() > 0) {
      payment.setAmount(request.getAmount());
    } else {
      throw new InternalException(ErrorMessage.PAYMENT_INVALID_AMOUNT);
    }

    payment.setIsPaid(false);

    EnumPaymentType type = EnumPaymentType.findByName(request.getType());
    if (type != null) {
      payment.setType(type.name());
    } else {
      throw new NotFoundException(ErrorMessage.PAYMENT_TYPE_NOT_FOUND);
    }

    payment.setPaymentDate(LocalDateTime.now());

    Payment _payment = paymentRepository.save(payment);
    return _payment;

  }

  @Override
  public Page<Payment> getPaymentsByUser(String username, PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<Payment> payments = paymentRepository.findByUser(username, page);
    return payments;
  }

  @Override
  public Page<Payment> getPaymentsByContract(Long id, String username, PaginationRequest request) {
    if (!contractRepository.existsById(id)) {
      throw new NotFoundException(ErrorMessage.CONTRACT_NOT_FOUND);
    }
    Page<Payment> payments = null;
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    String role = user.getRoles().iterator().next().getName();

    if (role.equalsIgnoreCase("ROLE_MODERATOR")) {
      payments = paymentRepository.findByContract(id, page);
    } else {
      payments = paymentRepository.findByContract(id, username, page);
    }
    return payments;
  }

  @Override
  public Page<Payment> searchPayments(PaginationRequest request, String search) {
    PaymentSpecificationsBuilder builder = new PaymentSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Payment> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Payment> pages = paymentRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public Payment editPayment(Long id, String username, Map<String, Object> updates) {
    Payment payment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.PAYMENT_NOT_FOUND));

    if (payment.getSender().getUsername().equals(username)) {
      String detail = String.valueOf(updates.get("detail"));
      if (updates.get("detail") != null && !Tool.isBlank(detail)) {
        payment.setDetail(detail);
      }

      String amount = String.valueOf(updates.get("amount"));
      if (updates.get("amount") != null && !Tool.isEqual(payment.getAmount(), amount)) {
        payment.setAmount(Double.valueOf(amount));
      }
    }

    if (payment.getRecipient().getUsername().equals(username)) {
      Boolean isPaidString = (Boolean) updates.get("isPaid");
      if (updates.get("isPaid") != null && isPaidString != null) {
        payment.setIsPaid(isPaidString);
      }
    }

    Payment _payment = paymentRepository.save(payment);
    return _payment;
  }

  @Override
  public void removePayment(Long id, String paymentname) {
    Payment payment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.PAYMENT_NOT_FOUND));
    Supplier sender = supplierRepository.findByUsername(paymentname)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    if (payment.getSender().equals(sender)) {
      paymentRepository.deleteById(id);
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
  }

}
