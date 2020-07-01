package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.BillOfLading;
import com.crm.models.Container;
import com.crm.models.ContainerType;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Inbound;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.BillOfLadingRequest;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.InboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerTypeRepository;
import com.crm.repository.DriverRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.InboundRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.PortRepository;
import com.crm.repository.ShippingLineRepository;
import com.crm.services.InboundService;

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
      Page<Inbound> pages = inboundRepository.getInboundsByFowarder(id, pageRequest);
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
    Page<Inbound> pages = inboundRepository.getInboundsByOutbound(shippingLine, containerType, pageRequest);
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

    if (request.getEmptyTime() != null && !request.getEmptyTime().isEmpty()) {
      LocalDateTime emptyTime = Tool.convertToLocalDateTime(request.getEmptyTime());
      inbound.setEmptyTime(emptyTime);
    }

    inbound.setReturnStation(request.getReturnStation());

    BillOfLading billOfLading = new BillOfLading();
    BillOfLadingRequest billOfLadingRequest = (BillOfLadingRequest) request.getBillOfLading();
    String billOfLadingNumber = billOfLadingRequest.getBillOfLadingNumber();
    if (billOfLadingNumber != null && !billOfLadingNumber.isEmpty()) {
      if (billOfLadingRepository.existsByBillOfLadingNumber(billOfLadingNumber)) {
        throw new DuplicateRecordException("Error: BillOfLading has been existed");
      }
      billOfLading.setBillOfLadingNumber(billOfLadingNumber);
    } else {
      throw new NotFoundException("ERROR: BillOfLadingNumber is not found.");
    }

    List<ContainerRequest> containersRequest = billOfLadingRequest.getContainers();

    for (int i = 0; i < containersRequest.size() - 1; i++) {
      for (int j = i + 1; j < containersRequest.size(); j++) {
        if (containersRequest.get(i).getContainerNumber().equals(containersRequest.get(j).getContainerNumber())
            || containersRequest.get(i).getLicensePlate().equals(containersRequest.get(j).getLicensePlate())
            || containersRequest.get(i).getDriver().equals(containersRequest.get(j).getDriver())) {
          throw new DuplicateRecordException("Error: Container has been existed");
        }
      }
    }

    LocalDateTime pickupTime = Tool.convertToLocalDateTime(request.getPickupTime());
    LocalDateTime freeTime = Tool.convertToLocalDateTime(request.getBillOfLading().getFreeTime());
    if (pickupTime.isAfter(freeTime)) {
      throw new InternalException("Error: pickupTime must before freeTime");
    }

    inbound.setPickupTime(pickupTime);
    billOfLading.setFreeTime(freeTime);

    for (int i = 0; i < containersRequest.size(); i++) {

      Container container = new Container();

      String containerNumber = containersRequest.get(i).getContainerNumber();
      String licensePlate = containersRequest.get(i).getLicensePlate();
      List<BillOfLading> billOfLadings = billOfLadingRepository.findAll();
      billOfLadings.forEach(item -> {
        Set<Container> containers = item.getContainers();
        containers.forEach(containerItem -> {
          if (containerNumber.equals(containerItem.getContainerNumber())
              || licensePlate.equals(containerItem.getLicensePlate())) {
            if (containerItem.getBillOfLading().getFreeTime().isBefore(pickupTime)
                || containerItem.getBillOfLading().getInbound().getPickupTime().isAfter(freeTime)) {
            } else {
              throw new InternalException(
                  String.format("Container %s has been busy", containerItem.getContainerNumber()));
            }
          }
        });
      });

      String driverUserName = containersRequest.get(i).getDriver();
      Driver driver = driverRepository.findByUsername(driverUserName)
          .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
      if (!driver.getForwarder().getId().equals(id)) {
        throw new NotFoundException("ERROR: The forwarder does not own this driver.");
      }
      container.setBillOfLading(billOfLading);

      List<Container> containers = containerRepository.findByDriver(driver.getId());
      containers.forEach(item -> {
        if (item.getBillOfLading().getFreeTime().isBefore(pickupTime)
            || item.getBillOfLading().getInbound().getPickupTime().isAfter(freeTime)) {
        } else {
          throw new InternalException(String.format("Driver %s has been busy", item.getDriver().getUsername()));
        }
      });

      container.setDriver(driver);
      container.setTractor(containersRequest.get(i).getTractor());
      container.setTrailer(containersRequest.get(i).getTrailer());
      container.setContainerNumber(containersRequest.get(i).getContainerNumber());
      container.setLicensePlate(containersRequest.get(i).getLicensePlate());
      container.setStatus(EnumSupplyStatus.CREATED.name());
      billOfLading.getContainers().add(container);

    }

    Port port = portRepository.findByNameCode(billOfLadingRequest.getPortOfDelivery())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    billOfLading.setPortOfDelivery(port);

    inbound.setBillOfLading(billOfLading);

    inboundRepository.save(inbound);
    return inbound;
  }

  @Override
  public Inbound updateInbound(InboundRequest request) {
    Inbound inbound = inboundRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Inbound is not found."));

    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    inbound.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    inbound.setContainerType(containerType);

    if (request.getEmptyTime() != null && !request.getEmptyTime().isEmpty()) {
      LocalDateTime emptyTime = Tool.convertToLocalDateTime(request.getEmptyTime());
      inbound.setEmptyTime(emptyTime);
    }

    inbound.setReturnStation(request.getReturnStation());

    BillOfLading billOfLading = (BillOfLading) inbound.getBillOfLading();
    BillOfLadingRequest billOfLadingRequest = (BillOfLadingRequest) request.getBillOfLading();

    if (billOfLadingRequest != null) {

      List<ContainerRequest> containersRequest = billOfLadingRequest.getContainers();

      for (int i = 0; i < containersRequest.size() - 1; i++) {
        for (int j = i + 1; j < containersRequest.size(); j++) {
          if (containersRequest.get(i).getContainerNumber().equals(containersRequest.get(j).getContainerNumber())
              || containersRequest.get(i).getLicensePlate().equals(containersRequest.get(j).getLicensePlate())
              || containersRequest.get(i).getDriver().equals(containersRequest.get(j).getDriver())) {
            throw new DuplicateRecordException("Error: Container has been existed!!!");
          }
        }
      }

      Set<Container> setContainers = inbound.getBillOfLading().getContainers();
      setContainers.forEach(item -> {
        for (int i = 0; i < containersRequest.size(); i++) {
          if (item.getContainerNumber().equals(containersRequest.get(i).getContainerNumber())
              || item.getLicensePlate().equals(containersRequest.get(i).getLicensePlate())
              || item.getDriver().getUsername().equals(containersRequest.get(i).getDriver())) {
            if (item.getId().equals(containersRequest.get(i).getId())) {

            } else {
              throw new DuplicateRecordException("Error: Container has been existed");
            }
          }
        }
      });

      LocalDateTime pickupTime = Tool.convertToLocalDateTime(request.getPickupTime());
      LocalDateTime freeTime = Tool.convertToLocalDateTime(request.getBillOfLading().getFreeTime());
      if (pickupTime.isAfter(freeTime)) {
        throw new InternalException("Error: pickupTime must before freeTime");
      }

      inbound.setPickupTime(pickupTime);
      billOfLading.setFreeTime(freeTime);

      for (int i = 0; i < containersRequest.size(); i++) {
        Container container = containerRepository.findById(containersRequest.get(i).getId())
            .orElseThrow(() -> new NotFoundException("ERROR: Container is not found."));

        String containerNumber = containersRequest.get(i).getContainerNumber();
        String licensePlate = containersRequest.get(i).getLicensePlate();
        List<BillOfLading> billOfLadings = billOfLadingRepository.findAll();
        billOfLadings.forEach(item -> {
          Set<Container> containers = item.getContainers();
          containers.forEach(containerItem -> {
            if (containerNumber.equals(containerItem.getContainerNumber())
                || licensePlate.equals(containerItem.getLicensePlate())) {
              if (containerItem.getBillOfLading().getFreeTime().isBefore(pickupTime)
                  || containerItem.getBillOfLading().getInbound().getPickupTime().isAfter(freeTime)) {
              } else {
                if (request.getId().equals(item.getInbound().getId())) {
                } else {
                  throw new InternalException(
                      String.format("Container %s has been busy", containerItem.getContainerNumber()));
                }
              }
            }
          });
        });

        String driverUserName = containersRequest.get(i).getDriver();
        Driver driver = driverRepository.findByUsername(driverUserName)
            .orElseThrow(() -> new NotFoundException("ERROR: Driver is not found."));
        if (!driver.getForwarder().getId().equals(inbound.getForwarder().getId())) {
          throw new NotFoundException("ERROR: The forwarder does not own this driver.");
        }
        container.setBillOfLading(billOfLading);

        List<Container> containers = containerRepository.findByDriver(driver.getId());
        containers.forEach(item -> {
          if (item.getBillOfLading().getFreeTime().isBefore(pickupTime)
              || item.getBillOfLading().getInbound().getPickupTime().isAfter(freeTime)) {
          } else {
            if (item.getBillOfLading().getInbound().getId().equals(request.getId())) {
            } else {
              throw new InternalException(String.format("Driver %s has been busy", item.getDriver().getUsername()));
            }
          }
        });

        container.setDriver(driver);
        container.setTractor(containersRequest.get(i).getTractor());
        container.setTrailer(containersRequest.get(i).getTrailer());
        container.setContainerNumber(containersRequest.get(i).getContainerNumber());
        container.setLicensePlate(containersRequest.get(i).getLicensePlate());
        container.setStatus(containersRequest.get(i).getStatus());
        billOfLading.getContainers().add(container);
      }

      Port port = portRepository.findByNameCode(billOfLadingRequest.getPortOfDelivery())
          .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
      billOfLading.setPortOfDelivery(port);

      inbound.setBillOfLading(billOfLading);

    }

    inboundRepository.save(inbound);
    return inbound;
  }

  @Override
  public Inbound editInbound(Map<String, Object> updates, Long id) {
    Inbound inbound = inboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Inbound is not found."));

    String shippingLineRequest = (String) updates.get("shippingLine");
    if (shippingLineRequest != null && !shippingLineRequest.isEmpty()) {
      ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(shippingLineRequest)
          .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
      inbound.setShippingLine(shippingLine);
    }

    String containerTypeRequest = (String) updates.get("containerType");
    if (containerTypeRequest != null && !containerTypeRequest.isEmpty()) {
      ContainerType containerType = containerTypeRepository.findByName(containerTypeRequest)
          .orElseThrow(() -> new NotFoundException("ERROR: Container Type is not found."));
      inbound.setContainerType(containerType);
    }

    String returnStationRequest = (String) updates.get("returnStation");
    if (returnStationRequest != null && !returnStationRequest.isEmpty()) {
      inbound.setReturnStation(returnStationRequest);
    }

    String pickupTimeRequest = (String) updates.get("pickupTime");
    if (pickupTimeRequest != null && !pickupTimeRequest.isEmpty()) {
      LocalDateTime pickupTime = Tool.convertToLocalDateTime(pickupTimeRequest);

      Set<Container> containers = inbound.getBillOfLading().getContainers();
      containers.forEach(item -> {

        String containerNumber = item.getContainerNumber();
        String licensePlate = item.getLicensePlate();
        List<BillOfLading> billOfLadings = billOfLadingRepository.findAll();
        billOfLadings.forEach(itemBillOfLadings -> {
          Set<Container> setContainer = itemBillOfLadings.getContainers();
          setContainer.forEach(containerItem -> {
            if (containerNumber.equals(containerItem.getContainerNumber())
                || licensePlate.equals(containerItem.getLicensePlate())) {
              if (containerItem.getBillOfLading().getFreeTime().isBefore(pickupTime) || containerItem.getBillOfLading()
                  .getInbound().getPickupTime().isAfter(inbound.getBillOfLading().getFreeTime())) {
              } else {
                if (containerItem.getBillOfLading().getId().equals(inbound.getId())) {
                } else {
                  throw new InternalException(
                      String.format("Container %s has been busy", containerItem.getContainerNumber()));
                }
              }
            }
          });
        });

        Long driverId = item.getDriver().getId();
        List<Container> listContainer = containerRepository.findByDriver(driverId);
        listContainer.forEach(container -> {
          if (container.getBillOfLading().getFreeTime().isBefore(pickupTime) || container.getBillOfLading().getInbound()
              .getPickupTime().isAfter(inbound.getBillOfLading().getFreeTime())) {
          } else {
            if (container.getBillOfLading().getId().equals(inbound.getId())) {
            } else {
              throw new InternalException(
                  String.format("Driver %s has been busy", container.getDriver().getUsername()));
            }
          }
        });
      });

      if (inbound.getBillOfLading().getFreeTime().isAfter(pickupTime)) {
        inbound.setPickupTime(pickupTime);
      } else {
        throw new InternalException("Error: pickupTime must before freeTime");
      }
    }

    String emptyTimeRequest = (String) updates.get("emptyTime");
    if (emptyTimeRequest != null && !emptyTimeRequest.isEmpty()) {
      LocalDateTime emptyTime = Tool.convertToLocalDateTime(emptyTimeRequest);
      inbound.setEmptyTime(emptyTime);
    }

    inboundRepository.save(inbound);
    return inbound;
  }

  @Override
  public void removeInbound(Long id) {
    Inbound inbound = inboundRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Inbound is not found."));

    Set<Container> containers = inbound.getBillOfLading().getContainers();
    containers.forEach(item -> {
      if (item.getStatus().equalsIgnoreCase(EnumSupplyStatus.COMBINED.name())) {
        throw new InternalException(String.format("Container %s has been combined", item.getContainerNumber()));
      }
    });
    inboundRepository.delete(inbound);
  }

}