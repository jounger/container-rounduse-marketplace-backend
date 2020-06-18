package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.NotFoundException;
import com.crm.models.Address;
import com.crm.models.Category;
import com.crm.models.Consignment;
import com.crm.models.ContainerType;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.ConsignmentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.CategoryRepository;
import com.crm.repository.ConsignmentRepository;
import com.crm.repository.ContainerTypeRepository;
import com.crm.repository.PortRepository;
import com.crm.repository.ShippingLineRepository;
import com.crm.services.ConsignmentService;

@Service
public class ConsignmentServiceImpl implements ConsignmentService {

  @Autowired
  private ConsignmentRepository consignmentRepository;

  @Autowired
  private ShippingLineRepository shippingLineRepository;

  @Autowired
  private ContainerTypeRepository containerTypeRepository;

  @Autowired
  private CategoryRepository categoryRepository;
  
  @Autowired
  private PortRepository portRepository;

  @Override
  public Page<Consignment> getListConsignment(PaginationRequest request) {
    Page<Consignment> pages = consignmentRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }
  

  @Override
  public void saveConsignment(ConsignmentRequest request) {
    
    Consignment consignment = new Consignment();

    ShippingLine shippingLine = shippingLineRepository.findByCompanyName(request.getShippingLineName())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    consignment.setShippingLine(shippingLine);
    
    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    consignment.setContainerType(containerType);

    consignment.setStatus(EnumSupplyStatus.findByName(request.getStatus()));

    LocalDateTime packingTime = Tool.convertToLocalDateTime(request.getPackingTime());
    consignment.setPackingTime(packingTime);


    Address packingStation = (Address) request.getPackingStation();
    consignment.setAddress(packingStation);

    consignment.setBookingNumber(request.getBookingNumber());
    
    LocalDateTime layTime = Tool.convertToLocalDateTime(request.getLaytime());
    consignment.setLayTime(layTime);
    LocalDateTime cutOftime = Tool.convertToLocalDateTime(request.getCutOfTime());
    consignment.setCutOfTime(cutOftime);
    
    consignment.setPayload(request.getPayload());
    consignment.setUnitOfMeasurement(request.getUnitOfMeasurement());

    Set<String> categoryListString = request.getCategories();
    Set<Category> listCategory = new HashSet<>();

    categoryListString.forEach(item -> {
      Category category = categoryRepository.findByName(item)
          .orElseThrow(() -> new NotFoundException("Error: Category is not found"));
      listCategory.add(category);
    });
    consignment.setCategoryList(listCategory);
    
    consignment.setFcl(true);
    
    Port port = portRepository.findByName(request.getPortOfLoading())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    consignment.setPort(port);
    
    consignmentRepository.save(consignment);
    
  }

  @Override
  public void editConsignment(ConsignmentRequest request) {
    
    Consignment consignment = consignmentRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Consignment is not found."));
    
    ShippingLine shippingLine = shippingLineRepository.findByCompanyName(request.getShippingLineName())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    consignment.setShippingLine(shippingLine);
    
    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    consignment.setContainerType(containerType);

    consignment.setStatus(EnumSupplyStatus.findByName(request.getStatus()));

    LocalDateTime packingTime = Tool.convertToLocalDateTime(request.getPackingTime());
    consignment.setPackingTime(packingTime);

    Address packingStation = (Address) request.getPackingStation();
    if (packingStation == null) {
      throw new NotFoundException("Error: PackingStation is not found");
    } else {
      consignment.setAddress(packingStation);
    }

    consignment.setBookingNumber(request.getBookingNumber());
    
    LocalDateTime layTime = Tool.convertToLocalDateTime(request.getLaytime());
    consignment.setLayTime(layTime);
    LocalDateTime cutOftime = Tool.convertToLocalDateTime(request.getCutOfTime());
    consignment.setCutOfTime(cutOftime);
    
    consignment.setPayload(request.getPayload());
    consignment.setUnitOfMeasurement(request.getUnitOfMeasurement());

    Set<String> categoryListString = request.getCategories();
    Set<Category> listCategory = new HashSet<>();

    categoryListString.forEach(categories -> {
      Category category = categoryRepository.findByName(categories)
          .orElseThrow(() -> new NotFoundException("Error: Category is not found"));
      listCategory.add(category);
    });
    consignment.setCategoryList(listCategory);
    
    consignment.setFcl(true);
    
    Port port = portRepository.findByName(request.getPortOfLoading())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    consignment.setPort(port);
    
    consignmentRepository.save(consignment);
    
  }

  @Override
  public void deleteConsignment(Long id) {
    Consignment consignment = consignmentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Consignment is not found."));
    consignmentRepository.delete(consignment);
  }

  @Override
  public Consignment findConsignmentById(Long id) {
    Consignment consignment = consignmentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Consignment is not found."));
    return consignment;    
  }
}
