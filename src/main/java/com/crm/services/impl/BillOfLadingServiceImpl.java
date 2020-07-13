package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.BillOfLading;
import com.crm.models.Container;
import com.crm.models.Port;
import com.crm.payload.request.BillOfLadingRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.InboundRepository;
import com.crm.repository.PortRepository;
import com.crm.services.BillOfLadingService;
import com.crm.specification.builder.BillOfLadingSpecificationsBuilder;

@Service
public class BillOfLadingServiceImpl implements BillOfLadingService {

  @Autowired
  BillOfLadingRepository billOfLadingRepository;

  @Autowired
  PortRepository portRepository;

  @Autowired
  InboundRepository inboundRepository;

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private ForwarderRepository forwarderRepository;

  @Override
  public Page<BillOfLading> getBillOfLadingsByInbound(Long id, PaginationRequest request) {
    if (inboundRepository.existsById(id)) {
      PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
          Sort.by(Sort.Direction.DESC, "createdAt"));
      Page<BillOfLading> pages = billOfLadingRepository.findByInbound(id, pageRequest);
      return pages;
    } else {
      throw new NotFoundException("ERROR: Inbound is not found.");
    }
  }

  @Override
  public BillOfLading updateBillOfLading(Long userId, BillOfLadingRequest request) {
    if (forwarderRepository.existsById(userId)) {
      BillOfLading billOfLading = billOfLadingRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("ERROR: BillOfLading is not found."));

      if (!billOfLading.getInbound().getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned BillOfLading", userId));
      }

      Port port = portRepository.findByNameCode(request.getPortOfDelivery())
          .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
      billOfLading.setPortOfDelivery(port);

      String billOfLadingNumber = request.getBillOfLadingNumber();
      if (billOfLadingNumber != null && !billOfLadingNumber.isEmpty()) {
        if (billOfLadingRepository.existsByBillOfLadingNumber(billOfLadingNumber)) {
          if (billOfLadingNumber.equals(billOfLading.getBillOfLadingNumber())) {
          } else {
            throw new DuplicateRecordException("Error: BillOfLading has been existed");
          }
        }
        billOfLading.setBillOfLadingNumber(billOfLadingNumber);
      }

      if (request.getFreeTime() != null && !request.getFreeTime().isEmpty()) {
        LocalDateTime freeTime = Tool.convertToLocalDateTime(request.getFreeTime());

        Set<Container> containers = new HashSet<>(billOfLading.getContainers());
        containers.forEach(item -> {
          if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
              || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
            throw new InternalException(
                String.format("Container %s has been %s", item.getContainerNumber(), item.getStatus()));
          }

          String containerNumber = item.getContainerNumber();
          boolean isContainer = containerRepository.findByContainerNumber(billOfLading.getId(), containerNumber,
              billOfLading.getInbound().getPickupTime(), freeTime);
          if (!isContainer) {
            throw new InternalException(String.format("Container %s has been busy", containerNumber));
          }

          Long driverId = item.getDriver().getId();
          boolean listContainer = containerRepository.findByDriver(driverId, billOfLading.getInbound().getPickupTime(),
              freeTime, billOfLading.getId());
          if (!listContainer) {
            throw new InternalException(String.format("Driver %s has been busy", item.getDriver().getUsername()));
          }

          Long tractorId = item.getTractor().getId();
          boolean listContainerByTracTor = containerRepository.findByTractor(tractorId,
              billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
          if (!listContainerByTracTor) {
            throw new InternalException(String.format("Tractor %s has been busy", item.getTractor().getLicensePlate()));
          }

          Long trailerId = item.getTrailer().getId();
          boolean listContainerByTrailer = containerRepository.findByTrailer(trailerId,
              billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
          if (!listContainerByTrailer) {
            throw new InternalException(String.format("Trailer %s has been busy", item.getTrailer().getLicensePlate()));
          }

        });

        if (freeTime.isAfter(billOfLading.getInbound().getPickupTime())) {
          billOfLading.setFreeTime(freeTime);
        } else {
          throw new InternalException("Error: pickupTime must before freeTime");
        }
      }

      billOfLadingRepository.save(billOfLading);

      return billOfLading;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public BillOfLading editBillOfLading(Map<String, Object> updates, Long id, Long userId) {
    if (forwarderRepository.existsById(userId)) {
      BillOfLading billOfLading = billOfLadingRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: BillOfLading is not found."));

      if (!billOfLading.getInbound().getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned BillOfLading", userId));
      }

      String portOfDelivery = (String) updates.get("portOfDelivery");
      if (portOfDelivery != null && !portOfDelivery.isEmpty()) {
        Port port = portRepository.findByNameCode(portOfDelivery)
            .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
        billOfLading.setPortOfDelivery(port);
      }

      String billOfLadingNumber = (String) updates.get("billOfLadingNumber");
      if (billOfLadingNumber != null && !billOfLadingNumber.isEmpty()) {
        if (billOfLadingRepository.existsByBillOfLadingNumber(billOfLadingNumber)) {
          if (billOfLadingNumber.equals(billOfLading.getBillOfLadingNumber())) {
          } else {
            throw new DuplicateRecordException("Error: BillOfLading has been existed");
          }
        }
        billOfLading.setBillOfLadingNumber(billOfLadingNumber);
      }

      String freeTimeReq = (String) updates.get("freeTime");
      if (freeTimeReq != null && !freeTimeReq.isEmpty()) {

        LocalDateTime freeTime = Tool.convertToLocalDateTime(freeTimeReq);

        Set<Container> containers = new HashSet<>(billOfLading.getContainers());
        containers.forEach(item -> {

          if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
              || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
            throw new InternalException(
                String.format("Container %s has been %s", item.getContainerNumber(), item.getStatus()));
          }

          String containerNumber = item.getContainerNumber();
          boolean isContainer = containerRepository.findByContainerNumber(billOfLading.getId(), containerNumber,
              billOfLading.getInbound().getPickupTime(), freeTime);
          if (!isContainer) {
            throw new InternalException(String.format("Container %s has been busy", containerNumber));
          }

          Long driverId = item.getDriver().getId();
          boolean listContainerByDriver = containerRepository.findByDriver(driverId,
              billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
          if (!listContainerByDriver) {
            throw new InternalException(String.format("Driver %s has been busy", item.getDriver().getUsername()));
          }

          Long tractorId = item.getTractor().getId();
          boolean listContainerByTracTor = containerRepository.findByTractor(tractorId,
              billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
          if (!listContainerByTracTor) {
            throw new InternalException(String.format("Tractor %s has been busy", item.getTractor().getLicensePlate()));
          }

          Long trailerId = item.getTrailer().getId();
          boolean listContainerByTrailer = containerRepository.findByTrailer(trailerId,
              billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
          if (!listContainerByTrailer) {
            throw new InternalException(String.format("Trailer %s has been busy", item.getTrailer().getLicensePlate()));
          }
        });

        if (freeTime.isAfter(billOfLading.getInbound().getPickupTime())) {
          billOfLading.setFreeTime(freeTime);
        } else {
          throw new InternalException("Error: pickupTime must before freeTime");
        }
      }

      billOfLadingRepository.save(billOfLading);

      return billOfLading;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public BillOfLading getBillOfLadingByBillOfLadingNumber(String billOfLadingNumber) {
    BillOfLading billOfLading = billOfLadingRepository.findByBillOfLadingNumber(billOfLadingNumber)
        .orElseThrow(() -> new NotFoundException("ERROR: BillOfLading is not found."));
    return billOfLading;
  }

  @Override
  public BillOfLading getBillOfLadingById(Long id) {
    BillOfLading billOfLading = billOfLadingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: BillOfLading is not found."));
    return billOfLading;
  }

  @Override
  public Page<BillOfLading> searchBillOfLadings(PaginationRequest request, String search) {
    BillOfLadingSpecificationsBuilder builder = new BillOfLadingSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<BillOfLading> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<BillOfLading> pages = billOfLadingRepository.findAll(spec, page);
    // Return result
    return pages;
  }

}
