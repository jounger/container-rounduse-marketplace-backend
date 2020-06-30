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
import com.crm.repository.InboundRepository;
import com.crm.repository.PortRepository;
import com.crm.services.BillOfLaingService;

@Service
public class BillOfLadingServiceImpl implements BillOfLaingService {

  @Autowired
  BillOfLadingRepository billOfLadingRepository;

  @Autowired
  PortRepository portRepository;

  @Autowired
  InboundRepository inboundRepository;

  @Autowired
  private ContainerRepository containerRepository;

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
  public BillOfLading updateBillOfLading(BillOfLadingRequest request) {
    BillOfLading billOfLading = billOfLadingRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: BillOfLading is not found."));

    Port port = portRepository.findByNameCode(request.getPortOfDelivery())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    billOfLading.setPortOfDelivery(port);

    String billOfLadingNumber = request.getBillOfLadingNumber();
    if (billOfLadingNumber != null) {
      if (billOfLadingRepository.existsByBillOfLadingNumber(billOfLadingNumber)) {
        if (billOfLadingNumber.equals(billOfLading.getBillOfLadingNumber())) {
        } else {
          throw new DuplicateRecordException("Error: BillOfLading has been existed");
        }
      }
      billOfLading.setBillOfLadingNumber(billOfLadingNumber);
    }

    if (request.getFreeTime() != null) {
      LocalDateTime freeTime = Tool.convertToLocalDateTime(request.getFreeTime());

      Set<Container> containers = billOfLading.getContainers();
      containers.forEach(item -> {
        Long driverId = item.getDriver().getId();
        List<Container> listContainer = containerRepository.findByDriver(driverId);
        listContainer.forEach(container -> {
          if (container.getBillOfLading().getFreeTime().isBefore(billOfLading.getInbound().getPickupTime())
              || container.getBillOfLading().getInbound().getPickupTime().isAfter(freeTime)) {
          } else {
            if (container.getBillOfLading().getId().equals(request.getId())) {
            } else {
              throw new InternalException(
                  String.format("Driver %s has been busy", container.getDriver().getUsername()));
            }
          }
        });
      });
      if (freeTime.isAfter(billOfLading.getInbound().getPickupTime())) {
        billOfLading.setFreeTime(freeTime);
      } else {
        throw new InternalException("Error: pickupTime must before freeTime");
      }
    }

    billOfLadingRepository.save(billOfLading);

    return billOfLading;
  }

  @Override
  public BillOfLading editBillOfLading(Map<String, Object> updates, Long id) {
    BillOfLading billOfLading = billOfLadingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: BillOfLading is not found."));

    String portOfDelivery = (String) updates.get("portOfDelivery");
    if (portOfDelivery != null) {
      Port port = portRepository.findByNameCode(portOfDelivery)
          .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
      billOfLading.setPortOfDelivery(port);
    }

    String billOfLadingNumber = (String) updates.get("billOfLadingNumber");
    if (billOfLadingNumber != null) {
      if (billOfLadingRepository.existsByBillOfLadingNumber(billOfLadingNumber)) {
        if (billOfLadingNumber.equals(billOfLading.getBillOfLadingNumber())) {
        } else {
          throw new DuplicateRecordException("Error: BillOfLading has been existed");
        }
      }
      billOfLading.setBillOfLadingNumber(billOfLadingNumber);
    }

    String freeTimeReq = (String) updates.get("freeTime");
    if (freeTimeReq != null) {

      LocalDateTime freeTime = Tool.convertToLocalDateTime(freeTimeReq);

      Set<Container> containers = billOfLading.getContainers();
      containers.forEach(item -> {
        Long driverId = item.getDriver().getId();
        List<Container> listContainer = containerRepository.findByDriver(driverId);
        listContainer.forEach(container -> {
          if (container.getBillOfLading().getFreeTime().isBefore(billOfLading.getInbound().getPickupTime())
              || container.getBillOfLading().getInbound().getPickupTime().isAfter(freeTime)) {
          } else {
            if (container.getBillOfLading().getId().equals(id)) {
            } else {
              throw new InternalException(
                  String.format("Driver %s has been busy", container.getDriver().getUsername()));
            }
          }
        });
      });
      if (freeTime.isAfter(billOfLading.getInbound().getPickupTime())) {
        billOfLading.setFreeTime(freeTime);
      } else {
        throw new InternalException("Error: pickupTime must before freeTime");
      }
    }

    billOfLadingRepository.save(billOfLading);

    return billOfLading;
  }

}
