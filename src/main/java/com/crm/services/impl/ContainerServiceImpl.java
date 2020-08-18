package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumBiddingStatus;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BillOfLading;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.ContainerTractor;
import com.crm.models.Driver;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerSemiTrailerRepository;
import com.crm.repository.ContainerTractorRepository;
import com.crm.repository.DriverRepository;
import com.crm.services.BiddingDocumentService;
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

  @Autowired
  private BiddingDocumentRepository biddingDocumentRepository;

  @Autowired
  private BiddingDocumentService biddingDocumentService;

  @Autowired
  @Qualifier("cachedThreadPool")
  private ExecutorService executorService;

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
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));
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
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BILLOFLADING_NOT_FOUND));

    Set<Container> containers = new HashSet<>(billOfLading.getContainers());

    if (containers.size() == billOfLading.getUnit()) {
      throw new InternalException(ErrorMessage.CONTAINER_MORE_THAN_NEEDED);
    }

    containers.forEach(item -> {
      if (item.getNumber().equals(request.getNumber()) || item.getDriver().getUsername().equals(request.getDriver())
          || item.getTrailer().getLicensePlate().equals(request.getTrailer())
          || item.getTractor().getLicensePlate().equals(request.getTractor())) {
        throw new DuplicateRecordException(ErrorMessage.CONTAINER_ALREADY_EXISTS);
      }
    });

    String containerNumber = request.getNumber();
    boolean listContainer = containerRepository.findByNumber(containerNumber, billOfLading.getInbound().getPickupTime(),
        billOfLading.getFreeTime(), username);
    if (!listContainer) {
      throw new InternalException(ErrorMessage.CONTAINER_BUSY);
    }

    String driverUserName = request.getDriver();
    Driver driver = driverRepository.findByUsername(driverUserName)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.DRIVER_BUSY));
    if (!driver.getForwarder().getUsername().equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    String trailer = request.getTrailer();
    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailer)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.TRAILER_NOT_FOUND));
    if (!containerSemiTrailer.getForwarder().getUsername()
        .equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    String tractor = request.getTractor();
    ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractor)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.TRACTOR_NOT_FOUND));
    if (!containerTractor.getForwarder().getUsername().equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    boolean listContainerByDriver = containerRepository.findByDriver(driver.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
    if (!listContainerByDriver) {
      throw new InternalException(ErrorMessage.CONTAINER_BUSY);
    }

    boolean listContainerByTractor = containerRepository.findByTractor(containerTractor.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
    if (!listContainerByTractor) {
      throw new InternalException(ErrorMessage.TRACTOR_BUSY);
    }

    boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
    if (!listContainerByTrailer) {
      throw new InternalException(ErrorMessage.TRAILER_BUSY);
    }

    container.setDriver(driver);
    container.setTractor(containerTractor);
    container.setTrailer(containerSemiTrailer);
    container.setBillOfLading(billOfLading);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    container.setNumber(request.getNumber());

    Container _container = containerRepository.save(container);
    return _container;
  }

  @Override
  public Container updateContainer(String username, ContainerRequest request) {

    Container container = containerRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));

    if (!container.getBillOfLading().getInbound().getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
        || container.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
      throw new InternalException(ErrorMessage.CONTAINER_BUSY);
    }

    BillOfLading billOfLading = (BillOfLading) container.getBillOfLading();

    Set<Container> containers = new HashSet<>(billOfLading.getContainers());
    containers.forEach(item -> {
      if (item.getNumber().equals(request.getNumber()) || item.getDriver().getUsername().equals(request.getDriver())
          || item.getTrailer().getLicensePlate().equals(request.getTrailer())
          || item.getTractor().getLicensePlate().equals(request.getTractor())) {
        if (item.getId().equals(request.getId())) {

        } else {
          throw new DuplicateRecordException(ErrorMessage.CONTAINER_ALREADY_EXISTS);
        }
      }
    });

    String containerNumber = request.getNumber();
    boolean listContainer = containerRepository.findByNumber(billOfLading.getId(), username, containerNumber,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
    if (!listContainer) {
      throw new InternalException(ErrorMessage.CONTAINER_BUSY);
    }

    String driverUserName = request.getDriver();
    Driver driver = driverRepository.findByUsername(driverUserName)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.DRIVER_NOT_FOUND));
    if (!driver.getForwarder().getUsername().equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    String trailer = request.getTrailer();
    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailer)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.TRAILER_NOT_FOUND));
    if (!containerSemiTrailer.getForwarder().getUsername()
        .equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    String tractor = request.getTractor();
    ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractor)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.TRACTOR_NOT_FOUND));
    if (!containerTractor.getForwarder().getUsername().equals(billOfLading.getInbound().getForwarder().getUsername())) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    boolean listContainerByDriver = containerRepository.findByDriver(driver.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
    if (!listContainerByDriver) {
      throw new InternalException(ErrorMessage.DRIVER_BUSY);
    }

    boolean listContainerByTracTor = containerRepository.findByTractor(containerTractor.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
    if (!listContainerByTracTor) {
      throw new InternalException(ErrorMessage.TRACTOR_BUSY);
    }

    boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), username,
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
    if (!listContainerByTrailer) {
      throw new InternalException(ErrorMessage.TRAILER_BUSY);
    }

    if (request.getStatus() != null && !request.getStatus().isEmpty()) {
      container.setStatus(EnumSupplyStatus.findByName(request.getStatus()).name());
    }

    container.setDriver(driver);
    container.setTractor(containerTractor);
    container.setTrailer(containerSemiTrailer);
    container.setBillOfLading(billOfLading);

    container.setNumber(request.getNumber());

    containerRepository.save(container);

    return container;
  }

  @Override
  public void removeContainer(Long id, String username) {

    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));

    if (!container.getBillOfLading().getInbound().getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
        || container.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
      throw new InternalException(ErrorMessage.CONTAINER_BUSY);
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
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));

    if (!container.getBillOfLading().getInbound().getForwarder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
        || container.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
      throw new InternalException(ErrorMessage.CONTAINER_BUSY);
    }

    BillOfLading billOfLading = container.getBillOfLading();

    String containerNumber = String.valueOf(updates.get("containerNumber"));
    if (updates.get("containerNumber") != null && !Tool.isEqual(container.getNumber(), containerNumber)) {
      container.setNumber(containerNumber);
    }

    String driverRequest = String.valueOf(updates.get("driver"));
    if (updates.get("driver") != null && !Tool.isEqual(container.getDriver().getUsername(), driverRequest)) {
      Driver driver = driverRepository.findByUsername(driverRequest)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.DRIVER_NOT_FOUND));
      if (!driver.getForwarder().getUsername().equals(billOfLading.getInbound().getForwarder().getUsername())) {
        throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
      }

      boolean listContainerByDriver = containerRepository.findByDriver(driver.getId(), username,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
      if (!listContainerByDriver) {
        throw new InternalException(ErrorMessage.CONTAINER_BUSY);
      }
      container.setDriver(driver);
    }

    String trailerRequest = String.valueOf(updates.get("trailer"));
    if (updates.get("trailer") != null && !Tool.isEqual(container.getTrailer().getLicensePlate(), trailerRequest)) {

      ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailerRequest)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.TRAILER_NOT_FOUND));
      if (!containerSemiTrailer.getForwarder().getUsername()
          .equals(billOfLading.getInbound().getForwarder().getUsername())) {
        throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
      }

      boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), username,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
      if (!listContainerByTrailer) {
        throw new InternalException(ErrorMessage.TRAILER_BUSY);
      }
      container.setTrailer(containerSemiTrailer);
    }

    String tractorRequest = String.valueOf(updates.get("tractor"));
    if (updates.get("tractor") != null && !Tool.isEqual(container.getTractor().getLicensePlate(), tractorRequest)) {

      ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractorRequest)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.TRACTOR_NOT_FOUND));
      if (!containerTractor.getForwarder().getUsername()
          .equals(billOfLading.getInbound().getForwarder().getUsername())) {
        throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
      }

      boolean listContainerByTracTor = containerRepository.findByTractor(containerTractor.getId(), username,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
      if (!listContainerByTracTor) {
        throw new InternalException(ErrorMessage.TRACTOR_BUSY);
      }
      container.setTractor(containerTractor);
    }

    String status = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(container.getStatus(), status)) {
      container.setStatus(status);
    }

    Set<Container> containers = new HashSet<>(billOfLading.getContainers());
    containers.forEach(item -> {
      if (item.getNumber().equals(container.getNumber())
          || item.getDriver().getUsername().equals(container.getDriver().getUsername())
          || item.getTrailer().getLicensePlate().equals(container.getTrailer().getLicensePlate())
          || item.getTractor().getLicensePlate().equals(container.getTractor().getLicensePlate())) {
        if (item.getId().equals(id)) {

        } else {
          throw new DuplicateRecordException(ErrorMessage.CONTAINER_ALREADY_EXISTS);
        }
      }
    });

    boolean listContainer = containerRepository.findByNumber(billOfLading.getId(), username, container.getNumber(),
        billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
    if (!listContainer) {
      throw new InternalException(ErrorMessage.CONTAINER_BUSY);
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

  @Override
  public List<Container> updateExpiredContainerFromList(List<Container> containers) {
    List<Container> result = new ArrayList<Container>();
    for (Container container : containers) {
      if (!container.getBids().isEmpty()) {
        Bid bid = (Bid) container.getBids().toArray()[container.getBids().size() - 1];
        BiddingDocument biddingDocument = bid.getBiddingDocument();
        boolean existsCombinedContainer = biddingDocumentRepository.existsCombinedBid(biddingDocument.getId());
        if (biddingDocument.getBidClosing().isBefore(LocalDateTime.now()) && existsCombinedContainer
            && biddingDocument.getStatus().equals(EnumBiddingStatus.BIDDING.name())) {
          String status = EnumBiddingStatus.EXPIRED.name();
          biddingDocumentService.updateExpiredBiddingDocuments(biddingDocument.getId(), status);
          if (existsCombinedContainer) {
            container.setStatus(EnumSupplyStatus.COMBINED.name());
          } else {
            container.setStatus(EnumSupplyStatus.CREATED.name());
          }
        }
      }
      result.add(container);
    }
    return result;
  }
}
