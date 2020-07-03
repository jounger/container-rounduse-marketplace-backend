package com.crm.services.impl;

import java.util.List;
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
import com.crm.models.Driver;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
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
    Page<Container> pages = containerRepository.findContainersByBillOfLading(id, pageRequest);
    return pages;
  }

  @Override
  public Container createContainer(Long id, ContainerRequest request) {
    Container container = new Container();
    BillOfLading billOfLading = billOfLadingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: BillOfLading is not found."));

    Set<Container> containers = billOfLading.getContainers();
    containers.forEach(item -> {
      if (item.getContainerNumber().equals(request.getContainerNumber())
          || item.getDriver().getUsername().equals(request.getDriver())
          || item.getLicensePlate().equals(request.getLicensePlate())) {
        throw new DuplicateRecordException("Error: Container has been existed");
      }
    });

    String containerNumber = request.getContainerNumber();
    String licensePlate = request.getLicensePlate();
    List<BillOfLading> billOfLadings = billOfLadingRepository.findAll();
    billOfLadings.forEach(item -> {
      Set<Container> setContainer = item.getContainers();
      setContainer.forEach(containerItem -> {
        if (containerNumber.equals(containerItem.getContainerNumber())
            || licensePlate.equals(containerItem.getLicensePlate())) {
          if (containerItem.getBillOfLading().getFreeTime().isBefore(billOfLading.getInbound().getPickupTime())
              || containerItem.getBillOfLading().getInbound().getPickupTime().isAfter(billOfLading.getFreeTime())) {
          } else {
            throw new InternalException(
                String.format("Container %s has been busy", containerItem.getContainerNumber()));
          }
        }
      });
    });

    String driverUserName = request.getDriver();
    Driver driver = driverRepository.findByUsername(driverUserName)
        .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
    if (!driver.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
      throw new NotFoundException("ERROR: The forwarder does not own this driver.");
    }

    List<Container> listContainer = containerRepository.findByDriver(driver.getId());
    listContainer.forEach(item -> {
      if (item.getBillOfLading().getFreeTime().isBefore(billOfLading.getInbound().getPickupTime())
          || item.getBillOfLading().getInbound().getPickupTime().isAfter(billOfLading.getFreeTime())) {
      } else {
        if (item.getBillOfLading().getInbound().getId().equals(request.getId())) {
        } else {
          throw new InternalException(String.format("Driver %s has been busy", item.getDriver().getUsername()));
        }
      }
    });

    container.setDriver(driver);
    container.setBillOfLading(billOfLading);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    container.setContainerNumber(request.getContainerNumber());
    container.setTrailer(request.getTrailer());
    container.setTractor(request.getTractor());
    container.setLicensePlate(request.getLicensePlate());

    containerRepository.save(container);
    return container;
  }

  @Override
  public Container updateContainer(ContainerRequest request) {
    Container container = containerRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));

    BillOfLading billOfLading = (BillOfLading) container.getBillOfLading();

    Set<Container> containers = billOfLading.getContainers();
    containers.forEach(item -> {
      if (item.getContainerNumber().equals(request.getContainerNumber())
          || item.getDriver().getUsername().equals(request.getDriver())
          || item.getLicensePlate().equals(request.getLicensePlate())) {
        if (item.getId().equals(request.getId())) {

        } else {
          throw new DuplicateRecordException("Error: Container has been existed");
        }
      }
    });

    String containerNumber = request.getContainerNumber();
    String licensePlate = request.getLicensePlate();
    List<BillOfLading> billOfLadings = billOfLadingRepository.findAll();
    billOfLadings.forEach(item -> {
      Set<Container> setContainer = item.getContainers();
      setContainer.forEach(containerItem -> {
        if (containerNumber.equals(containerItem.getContainerNumber())
            || licensePlate.equals(containerItem.getLicensePlate())) {
          if (containerItem.getBillOfLading().getFreeTime().isBefore(billOfLading.getInbound().getPickupTime())
              || containerItem.getBillOfLading().getInbound().getPickupTime().isAfter(billOfLading.getFreeTime())) {
          } else {
            if (item.getId().equals(billOfLading.getId())) {
            } else {
              throw new InternalException(
                  String.format("Container %s has been busy", containerItem.getContainerNumber()));
            }
          }
        }
      });
    });

    String driverUserName = request.getDriver();
    Driver driver = driverRepository.findByUsername(driverUserName)
        .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
    if (!driver.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
      throw new NotFoundException("ERROR: The forwarder does not own this driver.");
    }

    List<Container> listContainer = containerRepository.findByDriver(driver.getId());
    listContainer.forEach(item -> {
      if (item.getBillOfLading().getFreeTime().isBefore(billOfLading.getInbound().getPickupTime())
          || item.getBillOfLading().getInbound().getPickupTime().isAfter(billOfLading.getFreeTime())) {
      } else {
        if (item.getBillOfLading().getId().equals(billOfLading.getId())) {
        } else {
          throw new InternalException(String.format("Driver %s has been busy", item.getDriver().getUsername()));
        }
      }
    });

    if (request.getStatus() != null && !request.getStatus().isEmpty()) {
      container.setStatus(EnumSupplyStatus.findByName(request.getStatus()).name());
    }

    container.setDriver(driver);

    container.setBillOfLading(billOfLading);
    container.setTrailer(request.getTrailer());
    container.setTractor(request.getTractor());
    container.setContainerNumber(containerNumber);
    container.setLicensePlate(licensePlate);

    containerRepository.save(container);

    return container;
  }

  @Override
  public void removeContainer(Long id) {
    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));
    if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())) {
      throw new InternalException(String.format("Container %s has been combined", container.getContainerNumber()));
    }
    containerRepository.delete(container);
  }

  @Override
  public Container editContainer(Map<String, Object> updates, Long id) {

    Container container = containerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));
    if (container.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())) {
      throw new InternalException(String.format("Container %s has been combined", container.getContainerNumber()));
    }

    BillOfLading billOfLading = (BillOfLading) container.getBillOfLading();

    String containerNumber = (String) updates.get("containerNumber");
    if (containerNumber != null && !containerNumber.isEmpty()) {
      container.setContainerNumber(containerNumber);
    }

    String driverRequest = (String) updates.get("driver");
    if (driverRequest != null && !driverRequest.isEmpty()) {
      Driver driver = driverRepository.findByUsername(driverRequest)
          .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
      if (!driver.getForwarder().getId().equals(billOfLading.getInbound().getForwarder().getId())) {
        throw new NotFoundException("ERROR: The forwarder does not own this driver.");
      }

      List<Container> listContainer = containerRepository.findByDriver(driver.getId());
      listContainer.forEach(item -> {
        if (item.getBillOfLading().getFreeTime().isBefore(billOfLading.getInbound().getPickupTime())
            || item.getBillOfLading().getInbound().getPickupTime().isAfter(billOfLading.getFreeTime())) {
        } else {
          if (item.getBillOfLading().getId().equals(billOfLading.getId())) {
          } else {
            throw new InternalException(String.format("Driver %s has been busy", item.getDriver().getUsername()));
          }
        }
      });

      container.setDriver(driver);
    }
    String trailer = (String) updates.get("trailer");
    if (trailer != null && !trailer.isEmpty()) {
      container.setTrailer(trailer);
    }

    String tractor = (String) updates.get("tractor");
    if (tractor != null && !tractor.isEmpty()) {
      container.setTractor(tractor);
    }

    String licensePlate = (String) updates.get("licensePlate");
    if (licensePlate != null && !licensePlate.isEmpty()) {
      container.setLicensePlate(licensePlate);
    }

    String status = (String) updates.get("status");
    if (status != null && !status.isEmpty()) {
      container.setStatus(status);
    }

    Set<Container> containers = billOfLading.getContainers();
    containers.forEach(item -> {
      if (item.getContainerNumber().equals(container.getContainerNumber())
          || item.getDriver().getUsername().equals(container.getDriver().getUsername())
          || item.getLicensePlate().equals(container.getLicensePlate())) {
        if (item.getId().equals(id)) {

        } else {
          throw new DuplicateRecordException("Error: Container has been existed");
        }
      }
    });

    List<BillOfLading> billOfLadings = billOfLadingRepository.findAll();
    billOfLadings.forEach(item -> {
      Set<Container> setContainer = item.getContainers();
      setContainer.forEach(containerItem -> {
        if (container.getContainerNumber().equals(containerItem.getContainerNumber())
            || container.getLicensePlate().equals(containerItem.getLicensePlate())) {
          if (containerItem.getBillOfLading().getFreeTime().isBefore(billOfLading.getInbound().getPickupTime())
              || containerItem.getBillOfLading().getInbound().getPickupTime().isAfter(billOfLading.getFreeTime())) {
          } else {
            if (item.getId().equals(billOfLading.getId())) {
            } else {
              throw new InternalException(
                  String.format("Container %s has been busy", containerItem.getContainerNumber()));
            }
          }
        }
      });
    });

    containerRepository.save(container);
    return container;
  }

  @Override
  public Page<Container> getContainersByBid(Long id, PaginationRequest request) {

    String status = request.getStatus();
    PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit(),
        Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Container> pages = null;

    if (status != null && !status.isEmpty()) {
      pages = containerRepository.findContainersByBid(id, status, pageRequest);
    } else {
      pages = containerRepository.findContainersByBid(id, pageRequest);
    }
    return pages;
  }
}
