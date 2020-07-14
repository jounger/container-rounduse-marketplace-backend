package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.ContainerTractor;
import com.crm.models.ContainerType;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Inbound;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.BillOfLadingRequest;
import com.crm.payload.request.InboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerSemiTrailerRepository;
import com.crm.repository.ContainerTractorRepository;
import com.crm.repository.ContainerTypeRepository;
import com.crm.repository.DriverRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.InboundRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.PortRepository;
import com.crm.repository.ShippingLineRepository;
import com.crm.services.InboundService;
import com.crm.specification.builder.InboundSpecificationsBuilder;

@Service
public class InboundServiceImpl implements InboundService {

  @Autowired
  private ForwarderRepository forwarderRepository;

  @Autowired
  private InboundRepository inboundRepository;

  @Autowired
  private ShippingLineRepository shippingLineRepository;

  @Autowired
  private ContainerTypeRepository containerTypeRepository;

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private BillOfLadingRepository billOfLadingRepository;

  @Autowired
  private PortRepository portRepository;

  @Autowired
  private DriverRepository driverRepository;

  @Autowired
  private OutboundRepository outboundRepository;

  @Autowired
  private ContainerSemiTrailerRepository containerSemiTrailerRepository;

  @Autowired
  private ContainerTractorRepository containerTractorRepository;

  @Override
  public Page<Inbound> getInbounds(PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Inbound> pages = inboundRepository.findAll(pageRequest);
    return pages;
  }

  @Override
  public Inbound getInboundById(Long id) {
    Inbound inbound = inboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Inbound is not found."));
    return inbound;
  }

  @Override
  public Page<Inbound> getInboundsForwarder(Long id, PaginationRequest request) {
    if (forwarderRepository.existsById(id)) {
      PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
          Sort.by(Sort.Direction.DESC, "createdAt"));
      Page<Inbound> pages = inboundRepository.findByFowarder(id, pageRequest);
      return pages;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public Page<Inbound> getInboundsByOutbound(Long id, PaginationRequest request) {
    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Outbound is not found."));
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    String shippingLine = outbound.getShippingLine().getCompanyCode();
    String containerType = outbound.getContainerType().getName();
    Page<Inbound> pages = inboundRepository.findByOutbound(shippingLine, containerType, pageRequest);
    return pages;
  }

  @Override
  public Inbound createInbound(Long id, InboundRequest request) {

    Inbound inbound = new Inbound();

    Forwarder forwarder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Forwarder is not found."));
    inbound.setForwarder(forwarder);

    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    inbound.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: ContainerType is not found."));
    inbound.setContainerType(containerType);

    inbound.setReturnStation(request.getReturnStation());

    LocalDateTime pickupTime = Tool.convertToLocalDateTime(request.getPickupTime());
    inbound.setPickupTime(pickupTime);

    LocalDateTime emptyTime = pickupTime.plusDays(1);
    inbound.setEmptyTime(emptyTime);

    BillOfLading billOfLading = new BillOfLading();
    BillOfLadingRequest billOfLadingRequest = request.getBillOfLading();
    String billOfLadingNumber = billOfLadingRequest.getBillOfLadingNumber();
    if (billOfLadingNumber != null && !billOfLadingNumber.isEmpty()) {
      if (billOfLadingRepository.existsByBillOfLadingNumber(billOfLadingNumber)) {
        throw new DuplicateRecordException("Error: BillOfLading has been existed");
      }
      billOfLading.setBillOfLadingNumber(billOfLadingNumber);
    } else {
      throw new NotFoundException("ERROR: BillOfLadingNumber is not found.");
    }

    billOfLading.setUnit(billOfLadingRequest.getUnit());

    Port port = portRepository.findByNameCode(billOfLadingRequest.getPortOfDelivery())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    billOfLading.setPortOfDelivery(port);

    LocalDateTime freeTime = Tool.convertToLocalDateTime(request.getBillOfLading().getFreeTime());
    if (pickupTime.isAfter(freeTime)) {
      throw new InternalException("Error: pickupTime must before freeTime");
    }
    billOfLading.setFreeTime(freeTime);
    billOfLading.setInbound(inbound);

    inbound.setBillOfLading(billOfLading);

    inboundRepository.save(inbound);
    return inbound;
  }

  @Override
  public Inbound updateInbound(Long id, InboundRequest request) {

    if (forwarderRepository.existsById(id)) {
      Inbound inbound = inboundRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("ERROR: Inbound is not found."));

      if (!inbound.getForwarder().getId().equals(id)) {
        throw new InternalException(String.format("Forwarder %s not owned Inbound", id));
      }

      ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
          .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
      inbound.setShippingLine(shippingLine);

      ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
          .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
      inbound.setContainerType(containerType);

      inbound.setReturnStation(request.getReturnStation());

      BillOfLading billOfLading = inbound.getBillOfLading();

      Set<Container> setContainers = new HashSet<>(billOfLading.getContainers());
      setContainers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(
              String.format("Container %s has been %s", item.getContainerNumber(), item.getStatus()));
        }
      });

      LocalDateTime pickupTime = Tool.convertToLocalDateTime(request.getPickupTime());
      LocalDateTime freeTime = billOfLading.getFreeTime();
      if (pickupTime.isAfter(freeTime)) {
        throw new InternalException("Error: pickupTime must before freeTime");
      }

      inbound.setPickupTime(pickupTime);

      LocalDateTime emptyTime = pickupTime.plusDays(1);
      inbound.setEmptyTime(emptyTime);

      Collection<Container> collectionContainers = billOfLading.getContainers();
      List<Container> containers = new ArrayList<>(collectionContainers);

      if (containers != null) {
        for (int i = 0; i < containers.size(); i++) {
          Container container = containerRepository.findById(containers.get(i).getId())
              .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));

          boolean listContainer = containerRepository.findByContainerNumber(billOfLading.getId(), id,
              container.getContainerNumber(), inbound.getPickupTime(), freeTime);
          if (!listContainer) {
            throw new InternalException(String.format("Container %s has been busy", container.getContainerNumber()));
          }

          String driverUserName = container.getDriver().getUsername();
          Driver driver = driverRepository.findByUsername(driverUserName)
              .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));

          String trailer = container.getTrailer().getLicensePlate();
          ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailer)
              .orElseThrow(() -> new NotFoundException("ERROR: ContainerSemiTrailer is not found."));

          String tractor = container.getTractor().getLicensePlate();
          ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractor)
              .orElseThrow(() -> new NotFoundException("ERROR: ContainerTractor is not found."));

          boolean containersByDriver = containerRepository.findByDriver(driver.getId(), id, inbound.getPickupTime(),
              freeTime, billOfLading.getId());
          if (!containersByDriver) {
            throw new InternalException(String.format("Driver %s has been busy", driverUserName));
          }

          boolean listContainerByTractor = containerRepository.findByTractor(containerTractor.getId(), id,
              inbound.getPickupTime(), freeTime, billOfLading.getId());
          if (!listContainerByTractor) {
            throw new InternalException(String.format("Tractor %s has been busy", tractor));
          }

          boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), id,
              inbound.getPickupTime(), freeTime, billOfLading.getId());
          if (!listContainerByTrailer) {
            throw new InternalException(String.format("Trailer %s has been busy", trailer));
          }
        }
      }
      inboundRepository.save(inbound);
      return inbound;

    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }

  }

  @Override
  public Inbound editInbound(Map<String, Object> updates, Long id, Long userId) {
    if (forwarderRepository.existsById(userId)) {
      Inbound inbound = inboundRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: Inbound is not found."));

      if (!inbound.getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned Inbound", id));
      }

      BillOfLading billOfLading = inbound.getBillOfLading();
      Set<Container> setContainers = new HashSet<>(billOfLading.getContainers());
      setContainers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(
              String.format("Container %s has been %s", item.getContainerNumber(), item.getStatus()));
        }
      });

      String shippingLineRequest = (String) updates.get("shippingLine");
      if (shippingLineRequest != null && !shippingLineRequest.isEmpty()
          && !shippingLineRequest.equals(inbound.getShippingLine().getCompanyCode())) {
        ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(shippingLineRequest)
            .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
        inbound.setShippingLine(shippingLine);
      }

      String containerTypeRequest = (String) updates.get("containerType");
      if (containerTypeRequest != null && !containerTypeRequest.isEmpty()
          && !containerTypeRequest.equals(inbound.getContainerType().getName())) {
        ContainerType containerType = containerTypeRepository.findByName(containerTypeRequest)
            .orElseThrow(() -> new NotFoundException("ERROR: Container Type is not found."));
        inbound.setContainerType(containerType);
      }

      String returnStationRequest = (String) updates.get("returnStation");
      if (returnStationRequest != null && !returnStationRequest.isEmpty()
          && !returnStationRequest.equals(inbound.getReturnStation())) {
        inbound.setReturnStation(returnStationRequest);
      }

      String pickupTimeRequest = (String) updates.get("pickupTime");
      if (pickupTimeRequest != null && !pickupTimeRequest.isEmpty()
          && !pickupTimeRequest.equals(Tool.convertLocalDateTimeToString(inbound.getPickupTime()))) {
        LocalDateTime pickupTime = Tool.convertToLocalDateTime(pickupTimeRequest);

        LocalDateTime emptyTime = pickupTime.plusDays(1);
        inbound.setEmptyTime(emptyTime);

        Set<Container> containers = new HashSet<>(billOfLading.getContainers());
        containers.forEach(item -> {

          String containerNumber = item.getContainerNumber();
          boolean listContainer = containerRepository.findByContainerNumber(billOfLading.getId(), userId,
              containerNumber, pickupTime, billOfLading.getFreeTime());
          if (!listContainer) {
            throw new InternalException(String.format("Container %s has been busy", containerNumber));
          }

          Long driverId = item.getDriver().getId();
          boolean listContainerByDriver = containerRepository.findByDriver(driverId, userId, pickupTime,
              billOfLading.getFreeTime(), billOfLading.getId());
          if (!listContainerByDriver) {
            throw new InternalException(String.format("Driver %s has been busy", item.getDriver().getUsername()));
          }

          Long tractorId = item.getTractor().getId();
          boolean listContainerByTractor = containerRepository.findByTractor(tractorId, userId, pickupTime,
              billOfLading.getFreeTime(), billOfLading.getId());
          if (!listContainerByTractor) {
            throw new InternalException(String.format("Tractor %s has been busy", item.getTractor().getLicensePlate()));

          }

          Long trailerId = item.getTrailer().getId();
          boolean listContainerByTrailer = containerRepository.findByTrailer(trailerId, userId, pickupTime,
              billOfLading.getFreeTime(), billOfLading.getId());
          if (!listContainerByTrailer) {
            throw new InternalException(String.format("Trailer %s has been busy", item.getTrailer().getLicensePlate()));

          }

        });

        if (inbound.getBillOfLading().getFreeTime().isAfter(pickupTime)) {
          inbound.setPickupTime(pickupTime);
        } else {
          throw new InternalException("Error: pickupTime must before freeTime");
        }
      }

      String emptyTimeRequest = (String) updates.get("emptyTime");
      if (emptyTimeRequest != null && !emptyTimeRequest.isEmpty()
          && !emptyTimeRequest.equals(Tool.convertLocalDateTimeToString(inbound.getEmptyTime()))) {
        LocalDateTime emptyTime = Tool.convertToLocalDateTime(emptyTimeRequest);
        inbound.setEmptyTime(emptyTime);
      }

      inboundRepository.save(inbound);
      return inbound;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public void removeInbound(Long id, Long userId) {
    if (forwarderRepository.existsById(userId)) {
      Inbound inbound = inboundRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: Inbound is not found."));

      if (!inbound.getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned Inbound", id));
      }

      BillOfLading billOfLading = inbound.getBillOfLading();
      Set<Container> containers = new HashSet<>(billOfLading.getContainers());
      containers.forEach(item -> {
        if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
            || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
          throw new InternalException(
              String.format("Container %s has been %s", item.getContainerNumber(), item.getStatus()));
        }
      });
      inboundRepository.delete(inbound);
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public Page<Inbound> getInboundsByOutboundAndForwarder(Long id, Long userId, PaginationRequest request) {
    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Outbound is not found."));
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    String shippingLine = outbound.getShippingLine().getCompanyCode();
    String containerType = outbound.getContainerType().getName();
    Page<Inbound> pages = inboundRepository.findByOutboundAndForwarder(userId, shippingLine, containerType,
        pageRequest);
    return pages;
  }

  @Override
  public Page<Inbound> searchInbounds(PaginationRequest request, String search) {
    InboundSpecificationsBuilder builder = new InboundSpecificationsBuilder();
    Pattern pattern = Pattern.compile(Constant.SEARCH_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
      // Chaining criteria
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }
    // Build specification
    Specification<Inbound> spec = builder.build();
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    // Filter with repository
    Page<Inbound> pages = inboundRepository.findAll(spec, page);
    // Return result
    return pages;
  }

}