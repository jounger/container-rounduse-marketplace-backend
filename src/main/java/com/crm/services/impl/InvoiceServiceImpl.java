package com.crm.services.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import com.crm.enums.EnumInvoiceType;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Contract;
import com.crm.models.Invoice;
import com.crm.models.Supplier;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.InvoiceRequest;
import com.crm.repository.ContractRepository;
import com.crm.repository.InvoiceRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;
import com.crm.services.InvoiceService;
import com.crm.specification.builder.InvoiceSpecificationsBuilder;

@Service
public class InvoiceServiceImpl implements InvoiceService {

  @Autowired
  private InvoiceRepository invoiceRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private ContractRepository contractRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public Invoice createInvoice(Long id, String username, InvoiceRequest request) {
    Invoice invoice = new Invoice();

    Contract contract = contractRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTRACT_NOT_FOUND));
    invoice.setContract(contract);

    Supplier merchant = contract.getSender();
    Supplier forwarder = contract.getCombined().getBid().getBidder();
    if (username.equals(merchant.getUsername())) {
      invoice.setSender(merchant);
      invoice.setRecipient(forwarder);
    } else if (username.equals(forwarder.getUsername())) {
      invoice.setSender(forwarder);
      invoice.setRecipient(merchant);
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    invoice.setDetail(request.getDetail());
    if (request.getAmount() > 0) {
      invoice.setAmount(request.getAmount());
    } else {
      throw new InternalException(ErrorMessage.PAYMENT_INVALID_AMOUNT);
    }

    invoice.setIsPaid(false);

    EnumInvoiceType type = EnumInvoiceType.findByName(request.getType());
    if (type != null) {
      invoice.setType(type.name());
    } else {
      throw new NotFoundException(ErrorMessage.PAYMENT_TYPE_NOT_FOUND);
    }

    if (request.getPaymentDate() == null) {
      invoice.setPaymentDate(LocalDateTime.now());
    } else {
      invoice.setPaymentDate(Tool.convertToLocalDateTime(request.getPaymentDate()));
    }

    Invoice _payment = invoiceRepository.save(invoice);
    return _payment;

  }

  @Override
  public Page<Invoice> getInvoicesByUser(String username, PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<Invoice> invoices = invoiceRepository.findByUser(username, page);
    return invoices;
  }

  @Override
  public Page<Invoice> getInvoicesByContract(Long id, String username, PaginationRequest request) {
    if (!contractRepository.existsById(id)) {
      throw new NotFoundException(ErrorMessage.CONTRACT_NOT_FOUND);
    }
    Page<Invoice> invoices = null;
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    String role = user.getRoles().iterator().next().getName();

    if (role.equalsIgnoreCase("ROLE_MODERATOR")) {
      invoices = invoiceRepository.findByContract(id, page);
    } else {
      invoices = invoiceRepository.findByContract(id, username, page);
    }
    return invoices;
  }

  @Override
  public Page<Invoice> searchInvoices(PaginationRequest request, String search) {
    InvoiceSpecificationsBuilder builder = new InvoiceSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Invoice> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Invoice> pages = invoiceRepository.findAll(spec, page);
    // Return result
    return pages;
  }

  @Override
  public Invoice editInvoice(Long id, String username, Map<String, Object> updates) {
    Invoice invoice = invoiceRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.PAYMENT_NOT_FOUND));

    if (invoice.getSender().getUsername().equals(username)) {
      String detail = String.valueOf(updates.get("detail"));
      if (updates.get("detail") != null && !Tool.isBlank(detail)) {
        invoice.setDetail(detail);
      }

      String amount = String.valueOf(updates.get("amount"));
      if (updates.get("amount") != null && !Tool.isEqual(invoice.getAmount(), amount)) {
        invoice.setAmount(Double.valueOf(amount));
      }
    }

    if (invoice.getRecipient().getUsername().equals(username)) {
      Boolean isPaidString = (Boolean) updates.get("isPaid");
      if (updates.get("isPaid") != null && isPaidString != null) {
        Contract contract = invoice.getContract();
        Double percent = contract.getPaymentPercentage() + invoice.getAmount() / contract.getPrice() * 100;
        NumberFormat numberFormat = new DecimalFormat(Constant.CONTRACT_PAID_PERCENTAGE_FORMAT);
        contract.setPaymentPercentage(Double.valueOf(numberFormat.format(percent)));
        contractRepository.save(contract);
        invoice.setIsPaid(isPaidString);
      }
    }

    Invoice _payment = invoiceRepository.save(invoice);
    return _payment;
  }

  @Override
  public void removeInvoice(Long id, String paymentname) {
    Invoice invoice = invoiceRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.PAYMENT_NOT_FOUND));
    Supplier sender = supplierRepository.findByUsername(paymentname)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    if (invoice.getSender().equals(sender)) {
      invoiceRepository.deleteById(id);
    } else {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
  }

}
