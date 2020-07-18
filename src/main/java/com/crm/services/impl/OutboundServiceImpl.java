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

  @Override
  public Outbound getOutboundById(Long id) {
    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Outbound is not found."));
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
  public Page<Outbound> getOutboundsByMerchant(Long userId, PaginationRequest request) {
    if (merchantRepository.existsById(userId)) {
      PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
          Sort.by(Sort.Direction.DESC, "createdAt"));
      String status = request.getStatus();
      Page<Outbound> pages = null;
      if (status != null && !status.isEmpty()) {
        pages = outboundRepository.findByMerchantId(userId, status, pageRequest);
      } else {
        pages = outboundRepository.findByMerchantId(userId, pageRequest);
      }
      return pages;
    } else {
      throw new NotFoundException("ERROR: Merchant is not found.");
    }
  }

  @Override
  public Outbound createOutbound(Long userId, OutboundRequest request) {
    Outbound outbound = new Outbound();

    Merchant merchant = merchantRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("ERROR: Merchant is not found."));
    outbound.setMerchant(merchant);

    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    outbound.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    outbound.setContainerType(containerType);

    outbound.setStatus(EnumSupplyStatus.CREATED.name());

    outbound.setGoodsDescription(request.getGoodsDescription());

    LocalDateTime packingTime = Tool.convertToLocalDateTime(request.getPackingTime());
    outbound.setPackingTime(packingTime);

    LocalDateTime deliveryTime = Tool.convertToLocalDateTime(request.getDeliveryTime());
    outbound.setDeliveryTime(deliveryTime);

    if (packingTime.isAfter(deliveryTime)) {
      throw new InternalException("Error: packingTime must before deliveryTime");
    }

    outbound.setPackingStation(request.getPackingStation());

    outbound.setGrossWeight(request.getGrossWeight());

    if (request.getUnitOfMeasurement() != null && !request.getUnitOfMeasurement().isEmpty()) {
      outbound.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    }

    Booking booking = new Booking();
    BookingRequest bookingRequest = (BookingRequest) request.getBooking();
    String bookingNumber = bookingRequest.getBookingNumber();
    if (bookingNumber != null && !bookingNumber.isEmpty()) {
      if (bookingRepository.existsByBookingNumber(bookingNumber)) {
        throw new DuplicateRecordException("Error: Booking has been existed");
      }
      booking.setBookingNumber(bookingNumber);
    } else {
      throw new NotFoundException("ERROR: Booking is not found.");
    }

    Port portOfLoading = portRepository.findByNameCode(bookingRequest.getPortOfLoading())
        .orElseThrow(() -> new NotFoundException("ERROR: PortOfLoading is not found."));
    booking.setPortOfLoading(portOfLoading);

    booking.setUnit(bookingRequest.getUnit());

    if (bookingRequest.getCutOffTime() != null && !bookingRequest.getCutOffTime().isEmpty()) {
      LocalDateTime cutOffTime = Tool.convertToLocalDateTime(bookingRequest.getCutOffTime());
      booking.setCutOffTime(cutOffTime);
    }

    booking.setIsFcl(bookingRequest.getIsFcl());
    booking.setOutbound(outbound);

    outbound.setBooking(booking);

    outboundRepository.save(outbound);
    return outbound;
  }

  @Override
  public Outbound updateOutbound(Long userId, OutboundRequest request) {
    if (merchantRepository.existsById(userId)) {

      Outbound outbound = outboundRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("ERROR: Outbound is not found."));

      if (!outbound.getMerchant().getId().equals(userId)) {
        throw new InternalException(String.format("Merchant %s not owned Inbound", userId));
      }

      if (outbound.getStatus().equals(EnumSupplyStatus.COMBINED.name())
          || outbound.getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(String.format("Outbound with bookingNumber %s has been %s",
            outbound.getBooking().getBookingNumber(), outbound.getStatus()));
      }

      ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
          .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
      outbound.setShippingLine(shippingLine);

      ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
          .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
      outbound.setContainerType(containerType);

      outbound.setStatus(EnumSupplyStatus.findByName(request.getStatus()).name());

      outbound.setGoodsDescription(request.getGoodsDescription());

      LocalDateTime packingTime = Tool.convertToLocalDateTime(request.getPackingTime());
      outbound.setPackingTime(packingTime);

      LocalDateTime deliveryTime = Tool.convertToLocalDateTime(request.getDeliveryTime());
      outbound.setDeliveryTime(deliveryTime);

      if (packingTime.isAfter(deliveryTime)) {
        throw new InternalException("Error: packingTime must before deliveryTime");
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
            .orElseThrow(() -> new NotFoundException("ERROR: PortOfLoading is not found."));
        booking.setPortOfLoading(portOfLoading);

        booking.setUnit(bookingRequest.getUnit());

        if (bookingRequest.getCutOffTime() != null && !bookingRequest.getCutOffTime().isEmpty()) {
          LocalDateTime cutOffTime = Tool.convertToLocalDateTime(bookingRequest.getCutOffTime());
          booking.setCutOffTime(cutOffTime);
        }

        booking.setIsFcl(bookingRequest.getIsFcl());

        outbound.setBooking(booking);
      }

      outboundRepository.save(outbound);
      return outbound;
    } else {
      throw new NotFoundException("ERROR: Merchant Type is not found.");
    }
  }

  @Override
  public Outbound editOutbound(Map<String, Object> updates, Long id, Long userId) {
    if (merchantRepository.existsById(userId)) {
      Outbound outbound = outboundRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: Outbound is not found."));

      if (!outbound.getMerchant().getId().equals(userId)) {
        throw new InternalException(String.format("Merchant %s not owned Inbound", userId));
      }

      if (outbound.getStatus().equals(EnumSupplyStatus.COMBINED.name())
          || outbound.getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(String.format("Outbound with bookingNumber %s has been %s",
            outbound.getBooking().getBookingNumber(), outbound.getStatus()));
      }

      String shippingLineRequest = String.valueOf(updates.get("shippingLine"));
      if (shippingLineRequest != null && !shippingLineRequest.isEmpty()) {
        ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(shippingLineRequest)
            .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
        outbound.setShippingLine(shippingLine);
      }

      String containerTypeRequest = String.valueOf(updates.get("containerType"));
      if (containerTypeRequest != null && !containerTypeRequest.isEmpty()) {
        ContainerType containerType = containerTypeRepository.findByName(containerTypeRequest)
            .orElseThrow(() -> new NotFoundException("ERROR: Container Type is not found."));
        outbound.setContainerType(containerType);
      }

      String statusRequest = String.valueOf(updates.get("status"));
      if (statusRequest != null && !statusRequest.isEmpty()) {
        outbound.setStatus(EnumSupplyStatus.findByName(statusRequest).name());
      }

      String packingTimeRequest = String.valueOf(updates.get("packingTime"));
      if (packingTimeRequest != null && !packingTimeRequest.isEmpty()) {
        LocalDateTime packingTime = Tool.convertToLocalDateTime(packingTimeRequest);
        outbound.setPackingTime(packingTime);
      }

      String packingStationRequest = String.valueOf(updates.get("packingStation"));
      if (packingStationRequest != null && !packingStationRequest.isEmpty()) {
        outbound.setPackingStation(packingStationRequest);
      }

      String goodsDescriptionRequest = String.valueOf(updates.get("goodsDescription"));
      if (goodsDescriptionRequest != null && !goodsDescriptionRequest.isEmpty()) {
        outbound.setGoodsDescription(goodsDescriptionRequest);
      }

      String grossWeightRequest = String.valueOf(updates.get("grossWeight"));
      if (grossWeightRequest != null && !grossWeightRequest.isEmpty()) {
        outbound.setGrossWeight(Double.valueOf(grossWeightRequest));
      }

      String deliveryTimeRequest = String.valueOf(updates.get("deliveryTime"));
      if (deliveryTimeRequest != null && !deliveryTimeRequest.isEmpty()) {
        LocalDateTime deliveryTime = Tool.convertToLocalDateTime(deliveryTimeRequest);
        outbound.setDeliveryTime(deliveryTime);
      }

      String unitOfMeasurementRequest = String.valueOf(updates.get("unitOfMeasurement"));
      if (unitOfMeasurementRequest != null && !unitOfMeasurementRequest.isEmpty()) {
        outbound.setUnitOfMeasurement(unitOfMeasurementRequest);
      }

      if (outbound.getPackingTime().isAfter(outbound.getDeliveryTime())) {
        throw new InternalException("Error: packingTime must before deliveryTime");
      }

      outboundRepository.save(outbound);
      return outbound;
    } else {
      throw new NotFoundException("ERROR: Merchant Type is not found.");
    }
  }

  @Override
  public void removeOutbound(Long id, Long userId) {
    if (merchantRepository.existsById(userId)) {
      Outbound outbound = outboundRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: Outbound is not found."));

      if (!outbound.getMerchant().getId().equals(userId)) {
        throw new InternalException(String.format("Merchant %s not owned Inbound", userId));
      }

      if (outbound.getStatus().equals(EnumSupplyStatus.COMBINED.name())
          || outbound.getStatus().equals(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(String.format("Outbound with bookingNumber %s has been %s",
            outbound.getBooking().getBookingNumber(), outbound.getStatus()));
      }
      outboundRepository.delete(outbound);
    } else {
      throw new NotFoundException("ERROR: Merchant Type is not found.");
    }
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
