package com.crm.services.impl;

import org.springframework.stereotype.Service;

import com.crm.services.OutboundService;

@Service
public class ConsignmentServiceImpl implements OutboundService {
  
  /*
  @Autowired
  private ConsignmentRepository consignmentRepository;

  @Autowired
  private ShippingLineRepository shippingLineRepository;

  @Autowired
  private ContainerTypeRepository containerTypeRepository;

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private PortRepository portRepository;

  @Override
  public Page<Outbound> getConsignments(PaginationRequest request) {
    Page<Outbound> pages = consignmentRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public void createConsignment(ConsignmentRequest request) {

    Outbound outbound = new Outbound();

    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    outbound.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    outbound.setContainerType(containerType);

    outbound.setStatus(EnumSupplyStatus.findByName(request.getStatus()));

    LocalDateTime packingTime = Tool.convertToLocalDateTime(request.getPackingTime());
    outbound.setPackingTime(packingTime);

    Address packingStation = (Address) request.getPackingStation();
    outbound.setPackingStation(packingStation);

    outbound.setBookingNumber(request.getBookingNumber());

    LocalDateTime layTime = Tool.convertToLocalDateTime(request.getLaytime());
    outbound.setLaytime(layTime);

    LocalDateTime cutOfftime = Tool.convertToLocalDateTime(request.getCutOffTime());
    outbound.setCutOffTime(cutOfftime);

    outbound.setPayload(request.getPayload());
    outbound.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()));

    Set<String> categoryListString = request.getCategories();
    Set<Category> listCategory = new HashSet<>();

    if (categoryListString != null) {
      categoryListString.forEach(item -> {
        Category category = categoryRepository.findByName(item)
            .orElseThrow(() -> new NotFoundException("Error: Category is not found"));
        listCategory.add(category);
      });
      outbound.setCategories(listCategory);
    }

    outbound.setFcl(true);

    Port port = portRepository.findByNameCode(request.getPortOfLoading())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    outbound.setPortOfLoading(port);

    consignmentRepository.save(outbound);

  }

  @Override
  public Outbound updateConsignment(ConsignmentRequest request) {

    Outbound outbound = consignmentRepository.findById(request.getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Consignment is not found."));

    ShippingLine shippingLine = shippingLineRepository.findByCompanyCode(request.getShippingLine())
        .orElseThrow(() -> new NotFoundException("ERROR: Shipping Line is not found."));
    outbound.setShippingLine(shippingLine);

    ContainerType containerType = containerTypeRepository.findByName(request.getContainerType())
        .orElseThrow(() -> new NotFoundException("ERROR: Type is not found."));
    outbound.setContainerType(containerType);

    outbound.setStatus(EnumSupplyStatus.findByName(request.getStatus()));

    LocalDateTime packingTime = Tool.convertToLocalDateTime(request.getPackingTime());
    outbound.setPackingTime(packingTime);

    Address packingStationReq = (Address) request.getPackingStation();
    Address packingStation = addressRepository.findById(outbound.getPackingStation().getId())
        .orElseThrow(() -> new NotFoundException("ERROR: Address is not found."));
    packingStation.setCity(packingStationReq.getCity());
    packingStation.setStreet(packingStationReq.getStreet());
    packingStation.setCounty(packingStationReq.getCounty());
    packingStation.setCountry(packingStationReq.getCountry());
    packingStation.setPostalCode(packingStationReq.getPostalCode());

//    consignment.setPackingStation(packingStation);

    outbound.setBookingNumber(request.getBookingNumber());

    LocalDateTime layTime = Tool.convertToLocalDateTime(request.getLaytime());
    outbound.setLaytime(layTime);

    LocalDateTime cutOfftime = Tool.convertToLocalDateTime(request.getCutOffTime());
    outbound.setCutOffTime(cutOfftime);

    outbound.setPayload(request.getPayload());
    outbound.setUnitOfMeasurement(EnumUnit.findByName(request.getUnitOfMeasurement()));

    Set<String> categoryListString = request.getCategories();
    Set<Category> listCategory = new HashSet<>();

    if (categoryListString != null) {
      categoryListString.forEach(categories -> {
        Category category = categoryRepository.findByName(categories)
            .orElseThrow(() -> new NotFoundException("Error: Category is not found"));
        listCategory.add(category);
      });
      outbound.setCategories(listCategory);
    }

    outbound.setFcl(true);

    Port port = portRepository.findByNameCode(request.getPortOfLoading())
        .orElseThrow(() -> new NotFoundException("ERROR: Port is not found."));
    outbound.setPortOfLoading(port);

    consignmentRepository.save(outbound);

    return outbound;
  }

  @Override
  public void removeConsignment(Long id) {
    if (consignmentRepository.existsById(id)) {
      consignmentRepository.deleteById(id);
    } else {
      throw new NotFoundException("ERROR: Consignment is not found.");
    }
  }

  @Override
  public Outbound getConsignmentById(Long id) {
    Outbound outbound = consignmentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("ERROR: Consignment is not found."));
    return outbound;
  }

  @Override
  public Page<Outbound> getConsignmentsByMerchant(Long id, PaginationRequest request) {
    Pageable pageable = PageRequest.of(request.getPage(), request.getLimit());
    Page<Outbound> pages = consignmentRepository.findByMerchantId(id, pageable);
    return pages;
  }
  */
}
