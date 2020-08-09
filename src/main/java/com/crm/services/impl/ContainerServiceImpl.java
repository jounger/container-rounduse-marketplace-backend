package com.crm.services.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BillOfLading;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.ContainerTractor;
import com.crm.models.Driver;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerSemiTrailerRepository;
import com.crm.repository.ContainerTractorRepository;
import com.crm.repository.DriverRepository;
import com.crm.services.ContainerService;

@Service
public class ContainerServiceImpl implements ContainerService {

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private DriverRepository driverRepository;

  @Autowired
  private BillOfLadingRepository billOfLadingRepository;

  @Autowired
  private ContainerSemiTrailerRepository containerSemiTrailerRepository;

  @Autowired
  private ContainerTractorRepository containerTractorRepository;

  @Autowired
  private BidRepository bidRepository;

  @Override
  public Page<Container> getContainersByInbound(Long id, PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Container> pages = containerRepository.findContainersByInbound(id, pageRequest);
    return pages;
  }

  @Override
  public Page<Container> getContainers(PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Container> pages = containerRepository.findAll(pageRequest);
    return pages;
  }

  @Override
  public Container getContainerById(Long id) {
    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_NOT_FOUND));
    return container;
  }

  @Override
  public Page<Container> getContainersByBillOfLading(Long id, PaginationRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Container> pages = containerRepository.findByBillOfLading(id, pageRequest);
    return pages;
  }

  @Override
  public Container createContainer(Long id, String username, ContainerRequest request) {

    Container container = new Container();

    BillOfLading billOfLading = billOfLadingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BILLOFLADING_NOT_FOUND));

    Set<Container> containers = new HashSet<>(billOfLading.getContainers());

    if (containers.size() == billOfLading.getUnit()) {
      throw new InternalException(ErrorConstant.CONTAINER_MORE_OR_LESS_THAN_NEEDED);
    }

    containers.forEach(item -> {
      if (item.getContainerNumber().equals(request.getContainerNumber())
          || item.getDriver().getUsername().equals(request.getDriver())
          || item.getTrailer().getLicensePlate().equals(request.getTrailer())
          || item.getTractor().getLicensePlate().equals(request.getTractor())) {
        throw new DuplicateRecordException(ErrorConstant.CONTAINER_ALREADY_EXISTS);
      }
    });

    String containerNumber = request.getContainerNumber();
    boolean listContainer = containerRepository.findByContainerNumber(containerNumber,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), username);
    if (!listContainer) {
      throw new InternalException(ErrorConstant.CONTAINER_BUSY);
    }

    String driverUserName = request.getDriver();
    Driver driver = driverRepository.findByUsername(driverUserName)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.DRIVER_BUSY));
    if (!driver.getForwarder().getUsername().equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    String trailer = request.getTrailer();
    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailer)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.TRAILER_NOT_FOUND));
    if (!containerSemiTrailer.getForwarder().getUsername()
        .equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    String tractor = request.getTractor();
    ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractor)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.TRACTOR_NOT_FOUND));
    if (!containerTractor.getForwarder().getUsername().equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    boolean listContainerByDriver = containerRepository.findByDriver(driver.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
    if (!listContainerByDriver) {
      throw new InternalException(ErrorConstant.CONTAINER_BUSY);
    }

    boolean listContainerByTractor = containerRepository.findByTractor(containerTractor.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
    if (!listContainerByTractor) {
      throw new InternalException(ErrorConstant.TRACTOR_BUSY);
    }

    boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
    if (!listContainerByTrailer) {
      throw new InternalException(ErrorConstant.TRAILER_BUSY);
    }

    container.setDriver(driver);
    container.setTractor(containerTractor);
    container.setTrailer(containerSemiTrailer);
    container.setBillOfLading(billOfLading);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    container.setContainerNumber(request.getContainerNumber());

    Container _container = containerRepository.save(container);
    return _container;
  }

  @Override
  public Container updateContainer(String username, ContainerRequest request) {

    Container container = containerRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_NOT_FOUND));

    if (!container.getBillOfLading().getInbound().getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
        || container.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
      throw new InternalException(ErrorConstant.CONTAINER_BUSY);
    }

    BillOfLading billOfLading = (BillOfLading) container.getBillOfLading();

    Set<Container> containers = new HashSet<>(billOfLading.getContainers());
    containers.forEach(item -> {
      if (item.getContainerNumber().equals(request.getContainerNumber())
          || item.getDriver().getUsername().equals(request.getDriver())
          || item.getTrailer().getLicensePlate().equals(request.getTrailer())
          || item.getTractor().getLicensePlate().equals(request.getTractor())) {
        if (item.getId().equals(request.getId())) {

        } else {
          throw new DuplicateRecordException(ErrorConstant.CONTAINER_ALREADY_EXISTS);
        }
      }
    });

    String containerNumber = request.getContainerNumber();
    boolean listContainer = containerRepository.findByContainerNumber(billOfLading.getId(), username, containerNumber,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
    if (!listContainer) {
      throw new InternalException(ErrorConstant.CONTAINER_BUSY);
    }

    String driverUserName = request.getDriver();
    Driver driver = driverRepository.findByUsername(driverUserName)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.DRIVER_NOT_FOUND));
    if (!driver.getForwarder().getUsername().equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    String trailer = request.getTrailer();
    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailer)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.TRAILER_NOT_FOUND));
    if (!containerSemiTrailer.getForwarder().getUsername()
        .equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    String tractor = request.getTractor();
    ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractor)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.TRACTOR_NOT_FOUND));
    if (!containerTractor.getForwarder().getUsername().equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    boolean listContainerByDriver = containerRepository.findByDriver(driver.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
    if (!listContainerByDriver) {
      throw new InternalException(ErrorConstant.DRIVER_BUSY);
    }

    boolean listContainerByTracTor = containerRepository.findByTractor(containerTractor.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
    if (!listContainerByTracTor) {
      throw new InternalException(ErrorConstant.TRACTOR_BUSY);
    }

    boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
    if (!listContainerByTrailer) {
      throw new InternalException(ErrorConstant.TRAILER_BUSY);
    }

    if (request.getStatus() != null && !request.getStatus().isEmpty()) {
      container.setStatus(EnumSupplyStatus.findByName(request.getStatus()).name());
    }

    container.setDriver(driver);
    container.setTractor(containerTractor);
    container.setTrailer(containerSemiTrailer);
    container.setBillOfLading(billOfLading);

    container.setContainerNumber(request.getContainerNumber());

    containerRepository.save(container);

    return container;
  }

  @Override
  public void removeContainer(Long id, String username) {

    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_NOT_FOUND));

    if (!container.getBillOfLading().getInbound().getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
        || container.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
      throw new InternalException(ErrorConstant.CONTAINER_BUSY);
    }

    Collection<Bid> bids = container.getBids();
    if (bids != null && bids.size() > 0) {
      bids.forEach(bid -> {
        bid.getContainers().remove(container);
        bidRepository.save(bid);
      });
    }
    containerRepository.delete(container);
  }

  @Override
  public Container editContainer(Map<String, Object> updates, Long id, String username) {

    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_NOT_FOUND));

    if (!container.getBillOfLading().getInbound().getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
    }

    if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
        || container.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
      throw new InternalException(ErrorConstant.CONTAINER_BUSY);
    }

    BillOfLading billOfLading = container.getBillOfLading();

    String containerNumber = String.valueOf(updates.get("containerNumber"));
    if (updates.get("containerNumber") != null && !Tool.isEqual(container.getContainerNumber(), containerNumber)) {
      container.setContainerNumber(containerNumber);
    }

    String driverRequest = String.valueOf(updates.get("driver"));
    if (updates.get("driver") != null && !Tool.isEqual(container.getDriver().getUsername(), driverRequest)) {
      Driver driver = driverRepository.findByUsername(driverRequest)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.DRIVER_NOT_FOUND));
      if (!driver.getForwarder().getUsername().equals(billOfLading.getInbound().getForwarder().getUsername())) {
        throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
      }

      boolean listContainerByDriver = containerRepository.findByDriver(driver.getId(), username,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
      if (!listContainerByDriver) {
        throw new InternalException(ErrorConstant.CONTAINER_BUSY);
      }
      container.setDriver(driver);
    }

    String trailerRequest = String.valueOf(updates.get("trailer"));
    if (updates.get("trailer") != null && !Tool.isEqual(container.getTrailer().getLicensePlate(), trailerRequest)) {

      ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailerRequest)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.TRAILER_NOT_FOUND));
      if (!containerSemiTrailer.getForwarder().getUsername()
          .equals(billOfLading.getInbound().getForwarder().getUsername())) {
        throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
      }

      boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), username,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
      if (!listContainerByTrailer) {
        throw new InternalException(ErrorConstant.TRAILER_BUSY);
      }
      container.setTrailer(containerSemiTrailer);
    }

    String tractorRequest = String.valueOf(updates.get("tractor"));
    if (updates.get("tractor") != null && !Tool.isEqual(container.getTractor().getLicensePlate(), tractorRequest)) {

      ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractorRequest)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.TRACTOR_NOT_FOUND));
      if (!containerTractor.getForwarder().getUsername()
          .equals(billOfLading.getInbound().getForwarder().getUsername())) {
        throw new ForbiddenException(ErrorConstant.USER_ACCESS_DENIED);
      }

      boolean listContainerByTracTor = containerRepository.findByTractor(containerTractor.getId(), username,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
      if (!listContainerByTracTor) {
        throw new InternalException(ErrorConstant.TRACTOR_BUSY);
      }
      container.setTractor(containerTractor);
    }

    String status = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(container.getStatus(), status)) {
      container.setStatus(status);
    }

    Set<Container> containers = new HashSet<>(billOfLading.getContainers());
    containers.forEach(item -> {
      if (item.getContainerNumber().equals(container.getContainerNumber())
          || item.getDriver().getUsername().equals(container.getDriver().getUsername())
          || item.getTrailer().getLicensePlate().equals(container.getTrailer().getLicensePlate())
          || item.getTractor().getLicensePlate().equals(container.getTractor().getLicensePlate())) {
        if (item.getId().equals(id)) {

        } else {
          throw new DuplicateRecordException(ErrorConstant.CONTAINER_ALREADY_EXISTS);
        }
      }
    });

    boolean listContainer = containerRepository.findByContainerNumber(billOfLading.getId(), username,
        container.getContainerNumber(), billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
    if (!listContainer) {
      throw new InternalException(ErrorConstant.CONTAINER_BUSY);
    }

    Container _container = containerRepository.save(container);
    return _container;
  }

  @Override
  public Page<Container> getContainersByBid(Long id, PaginationRequest request) {

    String status = request.getStatus();
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Container> pages = null;

    if (status != null && !status.isEmpty()) {
      pages = containerRepository.findByBid(id, status, pageRequest);
    } else {
      pages = containerRepository.findByBid(id, pageRequest);
    }
    return pages;
  }

  @Override
  public List<Container> getContainersByBidAndStatus(Long id, String status) {

    List<Container> containers = containerRepository.findByBidAndStatus(id, status);
    return containers;
  }
}
