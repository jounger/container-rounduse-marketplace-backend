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
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
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
import com.crm.repository.SupplyRepository;
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
  private PortRepository portRepository;

  @Autowired
  private DriverRepository driverRepository;

  @Autowired
  private OutboundRepository outboundRepository;

  @Autowired
  private ContainerSemiTrailerRepository containerSemiTrailerRepository;

  @Autowired
  private ContainerTractorRepository containerTractorRepository;

  @Autowired
  private SupplyRepository supplyRepository;

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
        .orElseThrow(() -> new NotFoundException(ErrorMessage.INBOUND_NOT_FOUND));
    return inbound;
  }

  @Override
  public Page<Inbound> getInboundsByForwarder(String username, PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Inbound> pages = inboundRepository.findByFowarder(username, pageRequest);
    return pages;
  }

  @Override
  public Page<Inbound> getInboundsByOutbound(Long id, PaginationRequest request) {
    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.OUTBOUND_NOT_FOUND));
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    String shippingLine = outbound.getShippingLine().getCompanyCode();
    String containerType = outbound.getContainerType().getName();
    Page<Inbound> pages = inboundRepository.findByOutbound(shippingLine, containerType, pageRequest);
    return pages;
  }

  @Override
  public Inbound createInbound(String username, InboundRequest request) {

    Inbound inbound = new Inbound();

    Forwarder forwarder = forwarderRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.FORWARDER_NOT_FOUND));
    inbound.setForwarder(forwarder);

    String code = request.getCode();
    if (supplyRepository.existsByCode(code)) {
      throw new DuplicateRecordException(ErrorMessage.SUPPLY_CODE_DUPLICATE);
    }
    inbound.setCode(code);

    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.SHIPPINGLINE_NOT_FOUND));
    inbound.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_TYPE_NOT_FOUND));
    inbound.setContainerType(containerType);

    inbound.setReturnStation(request.getReturnStation());

    LocalDateTime pickupTime = Tool.convertToLocalDateTime(request.getPickupTime());
    inbound.setPickupTime(pickupTime);

    LocalDateTime emptyTime = pickupTime.plusDays(1);
    inbound.setEmptyTime(emptyTime);

    BillOfLading billOfLading = new BillOfLading();
    BillOfLadingRequest billOfLadingRequest = request.getBillOfLading();
    String number = billOfLadingRequest.getNumber();
    if (number != null && !number.isEmpty()) {
      billOfLading.setNumber(number);
    } else {
      throw new NotFoundException(ErrorMessage.BILLOFLADING_NOT_FOUND);
    }

    billOfLading.setUnit(billOfLadingRequest.getUnit());

    Port port = portRepository.findByNameCode(billOfLadingRequest.getPortOfDelivery())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.PORT_NOT_FOUND));
    billOfLading.setPortOfDelivery(port);

    LocalDateTime freeTime = Tool.convertToLocalDateTime(request.getBillOfLading().getFreeTime());
    if (pickupTime.isAfter(freeTime)) {
      throw new InternalException(ErrorMessage.INBOUND_INVALID_FREETIME);
    }
    billOfLading.setFreeTime(freeTime);
    billOfLading.setInbound(inbound);

    inbound.setBillOfLading(billOfLading);

    Inbound _inbound = inboundRepository.save(inbound);
    return _inbound;
  }

  @Override
  public Inbound updateInbound(String username, InboundRequest request) {

    Inbound inbound = inboundRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.INBOUND_NOT_FOUND));

    if (!inbound.getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.SHIPPINGLINE_NOT_FOUND));
    inbound.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_TYPE_NOT_FOUND));
    inbound.setContainerType(containerType);

    inbound.setReturnStation(request.getReturnStation());

    BillOfLading billOfLading = inbound.getBillOfLading();

    Set<Container> setContainers = new HashSet<>(billOfLading.getContainers());
    setContainers.forEach(item -> {
      if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
          || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(ErrorMessage.CONTAINER_BUSY);
      }
    });

    LocalDateTime pickupTime = Tool.convertToLocalDateTime(request.getPickupTime());
    LocalDateTime freeTime = billOfLading.getFreeTime();
    if (pickupTime.isAfter(freeTime)) {
      throw new InternalException(ErrorMessage.INBOUND_INVALID_FREETIME);
    }

    inbound.setPickupTime(pickupTime);

    LocalDateTime emptyTime = pickupTime.plusDays(1);
    inbound.setEmptyTime(emptyTime);

    Collection<Container> collectionContainers = billOfLading.getContainers();
    List<Container> containers = new ArrayList<>(collectionContainers);

    if (containers != null) {
      for (int i = 0; i < containers.size(); i++) {
        Container container = containerRepository.findById(containers.get(i).getId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));

        boolean listContainer = containerRepository.findByNumber(billOfLading.getId(), username, container.getNumber(),
            inbound.getPickupTime(), freeTime);
        if (!listContainer) {
          throw new InternalException(ErrorMessage.CONTAINER_BUSY);
        }

        String driverUserName = container.getDriver().getUsername();
        Driver driver = driverRepository.findByUsername(driverUserName)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.DRIVER_NOT_FOUND));

        String trailer = container.getTrailer().getLicensePlate();
        ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailer)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TRAILER_NOT_FOUND));

        String tractor = container.getTractor().getLicensePlate();
        ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractor)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.TRACTOR_NOT_FOUND));

        boolean containersByDriver = containerRepository.findByDriver(driver.getId(), username, inbound.getPickupTime(),
            freeTime, billOfLading.getId(), EnumSupplyStatus.DELIVERED.name());
        if (!containersByDriver) {
          throw new InternalException(ErrorMessage.DRIVER_BUSY);
        }

        boolean listContainerByTractor = containerRepository.findByTractor(containerTractor.getId(), username,
            inbound.getPickupTime(), freeTime, billOfLading.getId(), EnumSupplyStatus.DELIVERED.name());
        if (!listContainerByTractor) {
          throw new InternalException(ErrorMessage.TRACTOR_BUSY);
        }

        boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), username,
            inbound.getPickupTime(), freeTime, billOfLading.getId(), EnumSupplyStatus.DELIVERED.name());
        if (!listContainerByTrailer) {
          throw new InternalException(ErrorMessage.TRAILER_BUSY);
        }
      }
    }
    Inbound _inbound = inboundRepository.save(inbound);
    return _inbound;

  }

  @Override
  public Inbound editInbound(Map<String, Object> updates, Long id, String username) {

    Inbound inbound = inboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.INBOUND_NOT_FOUND));

    if (!inbound.getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    BillOfLading billOfLading = inbound.getBillOfLading();
    Set<Container> setContainers = new HashSet<>(billOfLading.getContainers());
    setContainers.forEach(item -> {
      if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
          || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(ErrorMessage.INBOUND_IS_IN_TRANSACTION);
      }
    });

    String shippingLineRequest = String.valueOf(updates.get("shippingLine"));
    if (updates.get("shippingLine") != null
        && !Tool.isEqual(inbound.getShippingLine().getCompanyCode(), shippingLineRequest)) {
      ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(shippingLineRequest)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.SHIPPINGLINE_NOT_FOUND));
      inbound.setShippingLine(shippingLine);
    }

    String containerTypeRequest = String.valueOf(updates.get("containerType"));
    if (updates.get("containerType") != null
        && !Tool.isEqual(inbound.getContainerType().getName(), containerTypeRequest)) {
      ContainerType containerType = containerTypeRepository.findByName(containerTypeRequest)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_TYPE_NOT_FOUND));
      inbound.setContainerType(containerType);
    }

    String returnStationRequest = String.valueOf(updates.get("returnStation"));
    if (updates.get("returnStation") != null && !Tool.isEqual(inbound.getReturnStation(), returnStationRequest)) {
      inbound.setReturnStation(returnStationRequest);
    }

    String pickupTimeRequest = String.valueOf(updates.get("pickupTime"));
    if (updates.get("pickupTime") != null
        && !Tool.isEqual(String.valueOf(inbound.getPickupTime()), pickupTimeRequest)) {
      LocalDateTime pickupTime = Tool.convertToLocalDateTime(pickupTimeRequest);

      LocalDateTime emptyTime = pickupTime.plusDays(1);
      inbound.setEmptyTime(emptyTime);

      Set<Container> containers = new HashSet<>(billOfLading.getContainers());
      containers.forEach(item -> {

        String containerNumber = item.getNumber();
        boolean listContainer = containerRepository.findByNumber(billOfLading.getId(), username, containerNumber,
            pickupTime, billOfLading.getFreeTime());
        if (!listContainer) {
          throw new InternalException(ErrorMessage.CONTAINER_BUSY);
        }

        Long driverId = item.getDriver().getId();
        boolean listContainerByDriver = containerRepository.findByDriver(driverId, username, pickupTime,
            billOfLading.getFreeTime(), billOfLading.getId(), EnumSupplyStatus.DELIVERED.name());
        if (!listContainerByDriver) {
          throw new InternalException(ErrorMessage.DRIVER_BUSY);
        }

        Long tractorId = item.getTractor().getId();
        boolean listContainerByTractor = containerRepository.findByTractor(tractorId, username, pickupTime,
            billOfLading.getFreeTime(), billOfLading.getId(), EnumSupplyStatus.DELIVERED.name());
        if (!listContainerByTractor) {
          throw new InternalException(ErrorMessage.TRACTOR_BUSY);

        }

        Long trailerId = item.getTrailer().getId();
        boolean listContainerByTrailer = containerRepository.findByTrailer(trailerId, username, pickupTime,
            billOfLading.getFreeTime(), billOfLading.getId(), EnumSupplyStatus.DELIVERED.name());
        if (!listContainerByTrailer) {
          throw new InternalException(ErrorMessage.TRAILER_BUSY);

        }

      });

      if (inbound.getBillOfLading().getFreeTime().isAfter(pickupTime)) {
        inbound.setPickupTime(pickupTime);
      } else {
        throw new InternalException(ErrorMessage.INBOUND_INVALID_FREETIME);
      }
    }

    String emptyTimeRequest = String.valueOf(updates.get("emptyTime"));
    if (updates.get("emptyTime") != null && !Tool.isEqual(String.valueOf(inbound.getEmptyTime()), emptyTimeRequest)) {
      LocalDateTime emptyTime = Tool.convertToLocalDateTime(emptyTimeRequest);
      inbound.setEmptyTime(emptyTime);
    }

    Inbound _inbound = inboundRepository.save(inbound);
    return _inbound;
  }

  @Override
  public void removeInbound(Long id, String username) {

    Inbound inbound = inboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.INBOUND_NOT_FOUND));

    if (!inbound.getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    BillOfLading billOfLading = inbound.getBillOfLading();
    Set<Container> containers = new HashSet<>(billOfLading.getContainers());
    containers.forEach(item -> {
      if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
          || item.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(ErrorMessage.CONTAINER_BUSY);
      }
    });
    inboundRepository.delete(inbound);
  }

  @Override
  public Page<Inbound> getInboundsByOutboundAndForwarder(Long id, String username, PaginationRequest request) {
    Outbound outbound = outboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.OUTBOUND_NOT_FOUND));
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    String shippingLine = outbound.getShippingLine().getCompanyCode();
    String containerType = outbound.getContainerType().getName();
    List<String> status = new ArrayList<String>();
    status.add(EnumSupplyStatus.CREATED.name());
    Page<Inbound> pages = inboundRepository.findByOutboundAndForwarder(username, shippingLine, containerType, status,
        outbound.getPackingTime(), outbound.getBooking().getCutOffTime(), pageRequest);
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

  @Override
  public Inbound getInboundByContainer(Long id) {
    Inbound inbound = inboundRepository.findInboundByContainer(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.INBOUND_NOT_FOUND));
    return inbound;
  }

}