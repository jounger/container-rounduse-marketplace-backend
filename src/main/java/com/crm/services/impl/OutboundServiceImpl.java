package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.enums.EnumUnit;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Booking;
import com.crm.models.ContainerType;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.BookingRequest;
import com.crm.payload.request.OutboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BookingRepository;
import com.crm.repository.ContainerTypeRepository;
import com.crm.repository.MerchantRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.PortRepository;
import com.crm.repository.ShippingLineRepository;
import com.crm.repository.SupplyRepository;
import com.crm.services.OutboundService;
import com.crm.specification.builder.OutboundSpecificationsBuilder;

@Service
public class OutboundServiceImpl implements OutboundService {

  @Autowired
  private MerchantRepository merchantRepository;

  @Autowired
  private OutboundRepository outboundRepository;

  @Autowired
  private ShippingLineRepository shippingLineRepository;

  @Autowired
  private ContainerTypeRepository containerTypeRepository;

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private PortRepository portRepository;

  @Autowired
  private SupplyRepository supplyRepository;

  @Override
  public Outbound getOutboundById(Long id) {
    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.OUTBOUND_NOT_FOUND));
    return outbound;
  }

  @Override
  public Page<Outbound> getOutbounds(PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    String status = request.getStatus();
    Page<Outbound> pages = null;
    if (status != null && !status.isEmpty()) {
      pages = outboundRepository.findByStatus(status, pageRequest);
    } else {
      pages = outboundRepository.findAll(pageRequest);
    }
    return pages;
  }

  @Override
  public Page<Outbound> getOutboundsByMerchant(String username, PaginationRequest request) {

    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    String status = request.getStatus();
    Page<Outbound> pages = null;
    if (status != null && !status.isEmpty()) {
      pages = outboundRepository.findByMerchant(username, status, pageRequest);
    } else {
      pages = outboundRepository.findByMerchant(username, pageRequest);
    }
    return pages;

  }

  @Override
  public Outbound createOutbound(String username, OutboundRequest request) {
    Outbound outbound = new Outbound();

    Merchant merchant = merchantRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.MERCHANT_NOT_FOUND));
    outbound.setMerchant(merchant);

    String code = request.getCode();
    if (supplyRepository.existsByCode(code)) {
      throw new DuplicateRecordException(ErrorConstant.SUPPLY_CODE_DUPLICATE);
    }
    outbound.setCode(code);

    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.SHIPPINGLINE_NOT_FOUND));
    outbound.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_TYPE_NOT_FOUND));
    outbound.setContainerType(containerType);

    outbound.setStatus(EnumSupplyStatus.CREATED.name());

    outbound.setGoodsDescription(request.getGoodsDescription());

    LocalDateTime packingTime = Tool.convertToLocalDateTime(request.getPackingTime());
    outbound.setPackingTime(packingTime);

    LocalDateTime deliveryTime = Tool.convertToLocalDateTime(request.getDeliveryTime());
    outbound.setDeliveryTime(deliveryTime);

    if (packingTime.isAfter(deliveryTime)) {
      throw new InternalException(ErrorConstant.OUTBOUND_INVALID_DELIVERY_TIME);
    }

    outbound.setPackingStation(request.getPackingStation());

    outbound.setGrossWeight(request.getGrossWeight());

    if (request.getUnitOfMeasurement() != null && !request.getUnitOfMeasurement().isEmpty()) {
      outbound.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    }

    Booking booking = new Booking();
    BookingRequest bookingRequest = (BookingRequest) request.getBooking();
    String number = bookingRequest.getNumber();
    if (number != null && !number.isEmpty()) {
      if (bookingRepository.existsByNumber(number)) {
        throw new DuplicateRecordException(ErrorConstant.BOOKING_ALREADY_EXISTS);
      }
      booking.setNumber(number);
    } else {
      throw new NotFoundException(ErrorConstant.BOOKING_NOT_FOUND);
    }

    Port portOfLoading = portRepository.findByNameCode(bookingRequest.getPortOfLoading())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.PORT_NOT_FOUND));
    booking.setPortOfLoading(portOfLoading);

    booking.setUnit(bookingRequest.getUnit());

    if (bookingRequest.getCutOffTime() != null && !bookingRequest.getCutOffTime().isEmpty()) {
      LocalDateTime cutOffTime = Tool.convertToLocalDateTime(bookingRequest.getCutOffTime());
      booking.setCutOffTime(cutOffTime);
    }

    booking.setIsFcl(bookingRequest.getIsFcl());
    booking.setOutbound(outbound);

    outbound.setBooking(booking);

    Outbound _outbound = outboundRepository.save(outbound);
    return _outbound;
  }

  @Override
  public Outbound updateOutbound(String username, OutboundRequest request) {

    Outbound outbound = outboundRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.OUTBOUND_NOT_FOUND));

    if (!outbound.getMerchant().getUsername().equals(username)) {
      throw new InternalException(ErrorConstant.USER_ACCESS_DENIED);
    }

    if (outbound.getStatus().equals(EnumSupplyStatus.COMBINED.name())
        || outbound.getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
      throw new InternalException(ErrorConstant.OUTBOUND_IS_IN_TRANSACTION);
    }

    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.SHIPPINGLINE_NOT_FOUND));
    outbound.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_TYPE_NOT_FOUND));
    outbound.setContainerType(containerType);

    outbound.setStatus(EnumSupplyStatus.findByName(request.getStatus()).name());

    outbound.setGoodsDescription(request.getGoodsDescription());

    LocalDateTime packingTime = Tool.convertToLocalDateTime(request.getPackingTime());
    outbound.setPackingTime(packingTime);

    LocalDateTime deliveryTime = Tool.convertToLocalDateTime(request.getDeliveryTime());
    outbound.setDeliveryTime(deliveryTime);

    if (packingTime.isAfter(deliveryTime)) {
      throw new InternalException(ErrorConstant.OUTBOUND_INVALID_DELIVERY_TIME);
    }

    outbound.setPackingStation(request.getPackingStation());

    outbound.setGrossWeight(request.getGrossWeight());

    if (request.getUnitOfMeasurement() != null && !request.getUnitOfMeasurement().isEmpty()) {
      outbound.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    }

    Booking booking = (Booking) outbound.getBooking();
    BookingRequest bookingRequest = (BookingRequest) request.getBooking();

    if (bookingRequest != null) {

      Port portOfLoading = portRepository.findByNameCode(bookingRequest.getPortOfLoading())
          .orElseThrow(() -> new NotFoundException(ErrorConstant.PORT_NOT_FOUND));
      booking.setPortOfLoading(portOfLoading);

      booking.setUnit(bookingRequest.getUnit());

      if (bookingRequest.getCutOffTime() != null && !bookingRequest.getCutOffTime().isEmpty()) {
        LocalDateTime cutOffTime = Tool.convertToLocalDateTime(bookingRequest.getCutOffTime());
        booking.setCutOffTime(cutOffTime);
      }

      booking.setIsFcl(bookingRequest.getIsFcl());

      outbound.setBooking(booking);
    }

    Outbound _outbound = outboundRepository.save(outbound);
    return _outbound;

  }

  @Override
  public Outbound editOutbound(Map<String, Object> updates, Long id, String username) {

    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.OUTBOUND_NOT_FOUND));

    if (!outbound.getMerchant().getUsername().equals(username)) {
      throw new InternalException(ErrorConstant.USER_ACCESS_DENIED);
    }

    if (outbound.getStatus().equals(EnumSupplyStatus.COMBINED.name())
        || outbound.getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
      throw new InternalException(ErrorConstant.OUTBOUND_IS_IN_TRANSACTION);
    }

    String shippingLineRequest = String.valueOf(updates.get("shippingLine"));
    if (updates.get("shippingLine") != null
        && !Tool.isEqual(outbound.getShippingLine().getCompanyCode(), shippingLineRequest)) {
      ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(shippingLineRequest)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.SHIPPINGLINE_NOT_FOUND));
      outbound.setShippingLine(shippingLine);
    }

    String containerTypeRequest = String.valueOf(updates.get("containerType"));
    if (updates.get("containerType") != null
        && !Tool.isEqual(outbound.getContainerType().getName(), containerTypeRequest)) {
      ContainerType containerType = containerTypeRepository.findByName(containerTypeRequest)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_TYPE_NOT_FOUND));
      outbound.setContainerType(containerType);
    }

    String statusRequest = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(outbound.getStatus(), statusRequest)) {
      outbound.setStatus(EnumSupplyStatus.findByName(statusRequest).name());
    }

    String packingTimeRequest = String.valueOf(updates.get("packingTime"));
    if (updates.get("packingTime") != null
        && !Tool.isEqual(String.valueOf(outbound.getPackingTime()), packingTimeRequest)) {
      LocalDateTime packingTime = Tool.convertToLocalDateTime(packingTimeRequest);
      outbound.setPackingTime(packingTime);
    }

    String packingStationRequest = String.valueOf(updates.get("packingStation"));
    if (updates.get("packingStation") != null && !Tool.isEqual(outbound.getPackingStation(), packingStationRequest)) {
      outbound.setPackingStation(packingStationRequest);
    }

    String goodsDescriptionRequest = String.valueOf(updates.get("goodsDescription"));
    if (updates.get("goodsDescription") != null
        && !Tool.isEqual(outbound.getGoodsDescription(), goodsDescriptionRequest)) {
      outbound.setGoodsDescription(goodsDescriptionRequest);
    }

    String grossWeightRequest = String.valueOf(updates.get("grossWeight"));
    if (updates.get("grossWeight") != null && !Tool.isEqual(outbound.getGrossWeight(), grossWeightRequest)) {
      outbound.setGrossWeight(Double.valueOf(grossWeightRequest));
    }

    String deliveryTimeRequest = String.valueOf(updates.get("deliveryTime"));
    if (updates.get("deliveryTime") != null
        && !Tool.isEqual(String.valueOf(outbound.getDeliveryTime()), deliveryTimeRequest)) {
      LocalDateTime deliveryTime = Tool.convertToLocalDateTime(deliveryTimeRequest);
      outbound.setDeliveryTime(deliveryTime);
    }

    String unitOfMeasurementRequest = String.valueOf(updates.get("unitOfMeasurement"));
    if (updates.get("unitOfMeasurement") != null
        && !Tool.isEqual(outbound.getUnitOfMeasurement(), unitOfMeasurementRequest)) {
      outbound.setUnitOfMeasurement(unitOfMeasurementRequest);
    }

    if (outbound.getPackingTime().isAfter(outbound.getDeliveryTime())) {
      throw new InternalException(ErrorConstant.OUTBOUND_INVALID_DELIVERY_TIME);
    }

    Outbound _outbound = outboundRepository.save(outbound);
    return _outbound;

  }

  @Override
  public void removeOutbound(Long id, String username) {

    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.OUTBOUND_NOT_FOUND));

    if (!outbound.getMerchant().getUsername().equals(username)) {
      throw new InternalException(ErrorConstant.USER_ACCESS_DENIED);
    }

    if (outbound.getStatus().equals(EnumSupplyStatus.COMBINED.name())
        || outbound.getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
      throw new InternalException(ErrorConstant.OUTBOUND_IS_IN_TRANSACTION);
    }
    outboundRepository.delete(outbound);

  }

  @Override
  public Page<Outbound> searchOutbounds(PaginationRequest request, String search) {
    OutboundSpecificationsBuilder builder = new OutboundSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Outbound> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Outbound> pages = outboundRepository.findAll(spec, page);
    // Return result
    return pages;
  }
}
