package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    Page<Outbound> pages = outboundRepository.findAll(pageRequest);
    return pages;
  }

  @Override
  public Page<Outbound> getOutboundsByMerchant(Long id, PaginationRequest request) {
    if (merchantRepository.existsById(id)) {
      PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
          Sort.by(Sort.Direction.DESC, "createdAt"));
      Page<Outbound> pages = outboundRepository.findByMerchantId(id, pageRequest);
      return pages;
    } else {
      throw new NotFoundException("ERROR: Merchant is not found.");
    }
  }

  @Override
  public Outbound createOutbound(Long id, OutboundRequest request) {
    Outbound outbound = new Outbound();

    Merchant merchant = merchantRepository.findById(id)
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

    if (request.getPackingTime() != null) {
      LocalDateTime packingTime = Tool.convertToLocalDateTime(request.getPackingTime());
      outbound.setPackingTime(packingTime);
    }

    outbound.setPackingStation(request.getPackingStation());

    outbound.setPayload(request.getPayload());

    if (request.getUnitOfMeasurement() != null) {
      outbound.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    }

    Booking booking = new Booking();
    BookingRequest bookingRequest = (BookingRequest) request.getBooking();
    String bookingNumber = bookingRequest.getBookingNumber();
    if (bookingNumber != null) {
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

    if (bookingRequest.getCutOffTime() != null) {
      LocalDateTime cutOffTime = Tool.convertToLocalDateTime(bookingRequest.getCutOffTime());
      booking.setCutOffTime(cutOffTime);
    }

    booking.setIsFcl(bookingRequest.getIsFcl());

    outbound.setBooking(booking);

    outboundRepository.save(outbound);
    return outbound;
  }

  @Override
  public Outbound updateOutbound(OutboundRequest request) {

    Outbound outbound = outboundRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Outbound is not found."));

    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    outbound.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    outbound.setContainerType(containerType);

    outbound.setStatus(EnumSupplyStatus.findByName(request.getStatus()).name());

    outbound.setGoodsDescription(request.getGoodsDescription());

    if (request.getPackingTime() != null) {
      LocalDateTime packingTime = Tool.convertToLocalDateTime(request.getPackingTime());
      outbound.setPackingTime(packingTime);
    }

    outbound.setPackingStation(request.getPackingStation());

    outbound.setPayload(request.getPayload());

    if (request.getUnitOfMeasurement() != null) {
      outbound.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()).name());
    }

    Booking booking = (Booking) outbound.getBooking();
    BookingRequest bookingRequest = (BookingRequest) request.getBooking();

    if (bookingRequest != null) {

      Port portOfLoading = portRepository.findByNameCode(bookingRequest.getPortOfLoading())
          .orElseThrow(() -> new NotFoundException("ERROR: PortOfLoading is not found."));
      booking.setPortOfLoading(portOfLoading);

      booking.setUnit(bookingRequest.getUnit());

      if (bookingRequest.getCutOffTime() != null) {
        LocalDateTime cutOffTime = Tool.convertToLocalDateTime(bookingRequest.getCutOffTime());
        booking.setCutOffTime(cutOffTime);
      }

      booking.setIsFcl(bookingRequest.getIsFcl());

      outbound.setBooking(booking);
    }

    outboundRepository.save(outbound);
    return outbound;
  }

  @Override
  public Outbound editOutbound(Map<String, Object> updates, Long id) {
    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Outbound is not found."));

    String shippingLineRequest = (String) updates.get("shippingLine");
    if (shippingLineRequest != null) {
      ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(shippingLineRequest)
          .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
      outbound.setShippingLine(shippingLine);
    }

    String containerTypeRequest = (String) updates.get("containerType");
    if (containerTypeRequest != null) {
      ContainerType containerType = containerTypeRepository.findByName(containerTypeRequest)
          .orElseThrow(() -> new NotFoundException("ERROR: Container Type is not found."));
      outbound.setContainerType(containerType);
    }

    String statusRequest = (String) updates.get("status");
    if (statusRequest != null) {
      outbound.setStatus(EnumSupplyStatus.findByName(statusRequest).name());
    }

    String packingTimeRequest = (String) updates.get("packingTime");
    if (packingTimeRequest != null) {
      LocalDateTime packingTime = Tool.convertToLocalDateTime(packingTimeRequest);
      outbound.setPackingTime(packingTime);
    }

    String packingStationRequest = (String) updates.get("packingStation");
    if (packingStationRequest != null) {
      outbound.setPackingStation(packingStationRequest);
    }

    String goodsDescriptionRequest = (String) updates.get("goodsDescription");
    if (goodsDescriptionRequest != null) {
      outbound.setGoodsDescription(goodsDescriptionRequest);
    }

    Double payloadRequest = (Double) updates.get("payload");
    if (payloadRequest != null) {
      outbound.setPayload(payloadRequest);
    }

    String unitOfMeasurementRequest = (String) updates.get("unitOfMeasurement");
    if (unitOfMeasurementRequest != null) {
      outbound.setUnitOfMeasurement(unitOfMeasurementRequest);
    }

    outboundRepository.save(outbound);
    return outbound;
  }

  @Override
  public void removeOutbound(Long id) {

    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Outbound is not found."));
    if (outbound.getStatus().equals(EnumSupplyStatus.COMBINED.name())) {
      throw new InternalException(
          String.format("Outbound with bookingNumber %s has been combined", outbound.getBooking().getBookingNumber()));
    }
    outboundRepository.delete(outbound);
  }

}
