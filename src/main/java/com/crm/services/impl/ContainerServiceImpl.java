package com.crm.services.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.BillOfLading;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.ContainerTractor;
import com.crm.models.Driver;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerSemiTrailerRepository;
import com.crm.repository.ContainerTractorRepository;
import com.crm.repository.DriverRepository;
import com.crm.repository.ForwarderRepository;
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
  private ForwarderRepository forwarderRepository;

  @Autowired
  private ContainerSemiTrailerRepository containerSemiTrailerRepository;

  @Autowired
  private ContainerTractorRepository containerTractorRepository;

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
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));
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
  public Container createContainer(Long id, Long userId, ContainerRequest request) {

    if (forwarderRepository.existsById(userId)) {

      Container container = new Container();

      BillOfLading billOfLading = billOfLadingRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: BillOfLading is not found."));

      Set<Container> containers = new HashSet<>(billOfLading.getContainers());

      if (containers.size() == billOfLading.getUnit()) {
        throw new InternalException(
            String.format("BillOfLading %s has been full", billOfLading.getBillOfLadingNumber()));
      }

      containers.forEach(item -> {
        if (item.getContainerNumber().equals(request.getContainerNumber())
            || item.getDriver().getUsername().equals(request.getDriver())
            || item.getTrailer().getLicensePlate().equals(request.getTrailer())
            || item.getTractor().getLicensePlate().equals(request.getTractor())) {
          throw new DuplicateRecordException("Error: Container has been existed");
        }
      });

      String containerNumber = request.getContainerNumber();
      boolean listContainer = containerRepository.findByContainerNumber(containerNumber,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), userId);
      if (!listContainer) {
        throw new InternalException(String.format("Container %s has been busy", containerNumber));
      }

      String driverUserName = request.getDriver();
      Driver driver = driverRepository.findByUsername(driverUserName)
          .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
      if (!driver.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
        throw new NotFoundException("ERROR: The forwarder does not own this driver.");
      }

      String trailer = request.getTrailer();
      ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailer)
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerSemiTrailer is not found."));
      if (!containerSemiTrailer.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
        throw new NotFoundException("ERROR: The forwarder does not own this ContainerSemiTrailer.");
      }

      String tractor = request.getTractor();
      ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractor)
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerTractor is not found."));
      if (!containerTractor.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
        throw new NotFoundException("ERROR: The forwarder does not own this ContainerTractor.");
      }

      boolean listContainerByDriver = containerRepository.findByDriver(driver.getId(), userId,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
      if (!listContainerByDriver) {
        throw new InternalException(String.format("Driver %s has been busy", driverUserName));
      }

      boolean listContainerByTractor = containerRepository.findByTractor(containerTractor.getId(), userId,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
      if (!listContainerByTractor) {
        throw new InternalException(String.format("Tractor %s has been busy", tractor));
      }

      boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), userId,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
      if (!listContainerByTrailer) {
        throw new InternalException(String.format("Trailer %s has been busy", trailer));
      }

      container.setDriver(driver);
      container.setTractor(containerTractor);
      container.setTrailer(containerSemiTrailer);
      container.setBillOfLading(billOfLading);
      container.setStatus(EnumSupplyStatus.CREATED.name());

      container.setContainerNumber(request.getContainerNumber());

      containerRepository.save(container);
      return container;

    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public Container updateContainer(Long userId, ContainerRequest request) {

    if (forwarderRepository.existsById(userId)) {

      Container container = containerRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));

      if (!container.getBillOfLading().getInbound().getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned Container", userId));
      }

      if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
          || container.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(
            String.format("Container %s has been %s", container.getContainerNumber(), container.getStatus()));
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
            throw new DuplicateRecordException("Error: Container has been existed");
          }
        }
      });

      String containerNumber = request.getContainerNumber();
      boolean listContainer = containerRepository.findByContainerNumber(billOfLading.getId(), userId, containerNumber,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
      if (!listContainer) {
        throw new InternalException(String.format("Container %s has been busy", containerNumber));
      }

      String driverUserName = request.getDriver();
      Driver driver = driverRepository.findByUsername(driverUserName)
          .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
      if (!driver.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
        throw new NotFoundException("ERROR: The forwarder does not own this driver.");
      }

      String trailer = request.getTrailer();
      ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailer)
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerSemiTrailer is not found."));
      if (!containerSemiTrailer.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
        throw new NotFoundException("ERROR: The forwarder does not own this ContainerSemiTrailer.");
      }

      String tractor = request.getTractor();
      ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractor)
          .orElseThrow(() -> new NotFoundException("ERROR: ContainerTractor is not found."));
      if (!containerTractor.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
        throw new NotFoundException("ERROR: The forwarder does not own this ContainerTractor.");
      }

      boolean listContainerByDriver = containerRepository.findByDriver(driver.getId(), userId,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
      if (!listContainerByDriver) {
        throw new InternalException(String.format("Driver %s has been busy", driverUserName));
      }

      boolean listContainerByTracTor = containerRepository.findByTractor(containerTractor.getId(), userId,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
      if (!listContainerByTracTor) {
        throw new InternalException(String.format("ContainerTractor %s has been busy", tractor));
      }

      boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), userId,
          billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
      if (!listContainerByTrailer) {
        throw new InternalException(String.format("ContainerSemiTrailer %s has been busy", trailer));
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
    throw new NotFoundException("ERROR: Forwarder is not found.");
  }

  @Override
  public void removeContainer(Long id, Long userId) {

    if (forwarderRepository.existsById(userId)) {

      Container container = containerRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));

      if (!container.getBillOfLading().getInbound().getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned Container", userId));
      }

      if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
          || container.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(
            String.format("Container %s has been %s", container.getContainerNumber(), container.getStatus()));
      }
      containerRepository.delete(container);
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
  }

  @Override
  public Container editContainer(Map<String, Object> updates, Long id, Long userId) {

    if (forwarderRepository.existsById(userId)) {

      Container container = containerRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));

      if (!container.getBillOfLading().getInbound().getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned Container", userId));
      }

      if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())
          || container.getStatus().equalsIgnoreCase(EnumSupplyStatus.BIDDING.name())) {
        throw new InternalException(
            String.format("Container %s has been %s", container.getContainerNumber(), container.getStatus()));
      }

      BillOfLading billOfLading = (BillOfLading) container.getBillOfLading();

      String containerNumber = String.valueOf(updates.get("containerNumber"));
      if (containerNumber != null && !containerNumber.isEmpty()) {
        container.setContainerNumber(containerNumber);
      }

      String driverRequest = String.valueOf(updates.get("driver"));
      if (driverRequest != null && !driverRequest.isEmpty()) {
        Driver driver = driverRepository.findByUsername(driverRequest)
            .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
        if (!driver.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
          throw new NotFoundException("ERROR: The forwarder does not own this driver.");
        }

        boolean listContainerByDriver = containerRepository.findByDriver(driver.getId(), userId,
            billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
        if (!listContainerByDriver) {
          throw new InternalException(String.format("Driver %s has been busy", driverRequest));
        }
        container.setDriver(driver);
      }

      String trailerRequest = String.valueOf(updates.get("trailer"));
      if (trailerRequest != null && !trailerRequest.isEmpty()) {

        ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerRepository.findByLicensePlate(trailerRequest)
            .orElseThrow(() -> new NotFoundException("ERROR: ContainerSemiTrailer is not found."));
        if (!containerSemiTrailer.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
          throw new NotFoundException("ERROR: The forwarder does not own this ContainerSemiTrailer.");
        }

        boolean listContainerByTrailer = containerRepository.findByTrailer(containerSemiTrailer.getId(), userId,
            billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
        if (!listContainerByTrailer) {
          throw new InternalException(String.format("Trailer %s has been busy", trailerRequest));
        }
        container.setTrailer(containerSemiTrailer);
      }

      String tractorRequest = String.valueOf(updates.get("tractor"));
      if (tractorRequest != null && !tractorRequest.isEmpty()) {

        ContainerTractor containerTractor = containerTractorRepository.findByLicensePlate(tractorRequest)
            .orElseThrow(() -> new NotFoundException("ERROR: ContainerTractor is not found."));
        if (!containerTractor.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
          throw new NotFoundException("ERROR: The forwarder does not own this ContainerTractor.");
        }

        boolean listContainerByTracTor = containerRepository.findByTractor(containerTractor.getId(), userId,
            billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime(), billOfLading.getId());
        if (!listContainerByTracTor) {
          throw new InternalException(String.format("Tractor %s has been busy", tractorRequest));
        }
        container.setTractor(containerTractor);
      }

      String status = String.valueOf(updates.get("status"));
      if (status != null && !status.isEmpty()) {
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
            throw new DuplicateRecordException("Error: Container has been existed");
          }
        }
      });

      boolean listContainer = containerRepository.findByContainerNumber(billOfLading.getId(), userId,
          container.getContainerNumber(), billOfLading.getInbound().getPickupTime(), billOfLading.getFreeTime());
      if (!listContainer) {
        throw new InternalException(String.format("Container %s has been busy", containerNumber));
      }

      containerRepository.save(container);
      return container;
    } else {
      throw new NotFoundException("ERROR: Forwarder is not found.");
    }
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
}
