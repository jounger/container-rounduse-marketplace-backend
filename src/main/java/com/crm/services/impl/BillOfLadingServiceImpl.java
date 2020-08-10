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
import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.BillOfLading;
import com.crm.models.Container;
import com.crm.models.Port;
import com.crm.payload.request.BillOfLadingRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
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

  @Override
  public BillOfLading getBillOfLadingByInbound(Long id) {
    if (inboundRepository.existsById(id)) {
      BillOfLading billOfLading = billOfLadingRepository.findByInbound(id)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.BILLOFLADING_NOT_FOUND));
      return billOfLading;
    } else {
      throw new NotFoundException(ErrorConstant.INBOUND_NOT_FOUND);
    }
  }

  @Override
  public BillOfLading updateBillOfLading(String username, BillOfLadingRequest request) {

    BillOfLading billOfLading = billOfLadingRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BILLOFLADING_NOT_FOUND));

    if (!billOfLading.getInbound().getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    Port port = portRepository.findByNameCode(request.getPortOfDelivery())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.PORT_NOT_FOUND));
    billOfLading.setPortOfDelivery(port);

    String number = request.getNumber();
    if (number != null && !number.isEmpty()) {
      if (billOfLadingRepository.existsByNumber(number)) {
        if (number.equals(billOfLading.getNumber())) {
        } else {
          throw new DuplicateRecordException(ErrorConstant.BILLOFLADING_ALREADY_EXISTS);
        }
      }
      billOfLading.setNumber(number);
    }

    if (request.getUnit() < billOfLading.getContainers().size()) {
      throw new InternalException(ErrorConstant.CONTAINER_MORE_OR_LESS_THAN_NEEDED);
    }
    billOfLading.setUnit(request.getUnit());

    if (request.getFreeTime() != null && !request.getFreeTime().isEmpty()) {
      LocalDateTime freeTime = Tool.convertToLocalDateTime(request.getFreeTime());

      Set<Container> containers = new HashSet<>(billOfLading.getContainers());
      containers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(ErrorConstant.CONTAINER_BUSY);
        }

        String containerNumber = item.getContainerNumber();
        boolean isContainer = containerRepository.findByContainerNumber(billOfLading.getId(), username, containerNumber,
            billOfLading.getInbound().getPickupTime(), freeTime);
        if (!isContainer) {
          throw new InternalException(ErrorConstant.CONTAINER_BUSY);
        }

        Long driverId = item.getDriver().getId();
        boolean listContainer = containerRepository.findByDriver(driverId, username,
            billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
        if (!listContainer) {
          throw new InternalException(ErrorConstant.DRIVER_BUSY);
        }

        Long tractorId = item.getTractor().getId();
        boolean listContainerByTracTor = containerRepository.findByTractor(tractorId, username,
            billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
        if (!listContainerByTracTor) {
          throw new InternalException(ErrorConstant.TRACTOR_BUSY);
        }

        Long trailerId = item.getTrailer().getId();
        boolean listContainerByTrailer = containerRepository.findByTrailer(trailerId, username,
            billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
        if (!listContainerByTrailer) {
          throw new InternalException(ErrorConstant.TRAILER_BUSY);
        }

      });

      if (freeTime.isAfter(billOfLading.getInbound().getPickupTime())) {
        billOfLading.setFreeTime(freeTime);
      } else {
        throw new InternalException(ErrorConstant.BILLOFLADING_INVALID_FREE_TIME);
      }
    }

    BillOfLading _billOfLading = billOfLadingRepository.save(billOfLading);

    return _billOfLading;
  }

  @Override
  public BillOfLading editBillOfLading(Map<String, Object> updates, Long id, String username) {
    BillOfLading billOfLading = billOfLadingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BILLOFLADING_NOT_FOUND));

    if (!billOfLading.getInbound().getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    String portOfDelivery = String.valueOf(updates.get("portOfDelivery"));
    if (updates.get("portOfDelivery") != null
        && !Tool.isEqual(billOfLading.getPortOfDelivery().getNameCode(), portOfDelivery)) {
      Port port = portRepository.findByNameCode(portOfDelivery)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.PORT_NOT_FOUND));
      billOfLading.setPortOfDelivery(port);
    }

    String number = String.valueOf(updates.get("number"));
    if (updates.get("number") != null && !Tool.isEqual(billOfLading.getNumber(), number)) {
      if (billOfLadingRepository.existsByNumber(number)) {
        throw new DuplicateRecordException(ErrorConstant.BILLOFLADING_ALREADY_EXISTS);
      }
      billOfLading.setNumber(number);
    }

    String unitRequest = String.valueOf(updates.get("unit"));
    if (updates.get("unit") != null && !Tool.isEqual(billOfLading.getUnit(), unitRequest)) {
      int unit = Integer.parseInt(unitRequest);
      if (unit < billOfLading.getContainers().size()) {
        throw new InternalException(ErrorConstant.CONTAINER_MORE_OR_LESS_THAN_NEEDED);
      }
      billOfLading.setUnit(unit);
    }

    String freeTimeReq = String.valueOf(updates.get("freeTime"));
    if (updates.get("freeTime") != null && !Tool.isEqual(String.valueOf(billOfLading.getFreeTime()), freeTimeReq)) {

      LocalDateTime freeTime = Tool.convertToLocalDateTime(freeTimeReq);

      Set<Container> containers = new HashSet<>(billOfLading.getContainers());
      containers.forEach(item -> {

        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(ErrorConstant.CONTAINER_BUSY);
        }

        String containerNumber = item.getContainerNumber();
        boolean isContainer = containerRepository.findByContainerNumber(billOfLading.getId(), username, containerNumber,
            billOfLading.getInbound().getPickupTime(), freeTime);
        if (!isContainer) {
          throw new InternalException(ErrorConstant.CONTAINER_BUSY);
        }

        Long driverId = item.getDriver().getId();
        boolean listContainerByDriver = containerRepository.findByDriver(driverId, username,
            billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
        if (!listContainerByDriver) {
          throw new InternalException(ErrorConstant.DRIVER_BUSY);
        }

        Long tractorId = item.getTractor().getId();
        boolean listContainerByTracTor = containerRepository.findByTractor(tractorId, username,
            billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
        if (!listContainerByTracTor) {
          throw new InternalException(ErrorConstant.TRACTOR_BUSY);
        }

        Long trailerId = item.getTrailer().getId();
        boolean listContainerByTrailer = containerRepository.findByTrailer(trailerId, username,
            billOfLading.getInbound().getPickupTime(), freeTime, billOfLading.getId());
        if (!listContainerByTrailer) {
          throw new InternalException(ErrorConstant.TRAILER_BUSY);
        }
      });

      if (freeTime.isAfter(billOfLading.getInbound().getPickupTime())) {
        billOfLading.setFreeTime(freeTime);
      } else {
        throw new InternalException(ErrorConstant.BILLOFLADING_INVALID_FREE_TIME);
      }
    }

    billOfLadingRepository.save(billOfLading);

    return billOfLading;
  }

  @Override
  public BillOfLading getBillOfLadingByNumber(String number) {
    BillOfLading billOfLading = billOfLadingRepository.findByNumber(number)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BILLOFLADING_NOT_FOUND));
    return billOfLading;
  }

  @Override
  public BillOfLading getBillOfLadingById(Long id) {
    BillOfLading billOfLading = billOfLadingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BILLOFLADING_NOT_FOUND));
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
