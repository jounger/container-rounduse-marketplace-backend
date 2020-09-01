package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.crm.common.Tool;
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
import com.crm.repository.SupplyRepository;

public class InboundServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(InboundServiceImplTest.class);

  @InjectMocks
  InboundServiceImpl inboundServiceImpl;

  @Mock
  private ForwarderRepository forwarderRepository;

  @Mock
  private InboundRepository inboundRepository;

  @Mock
  private ShippingLineRepository shippingLineRepository;

  @Mock
  private ContainerTypeRepository containerTypeRepository;

  @Mock
  private ContainerRepository containerRepository;

  @Mock
  private BillOfLadingRepository billOfLadingRepository;

  @Mock
  private PortRepository portRepository;

  @Mock
  private DriverRepository driverRepository;

  @Mock
  private OutboundRepository outboundRepository;

  @Mock
  private ContainerSemiTrailerRepository containerSemiTrailerRepository;

  @Mock
  private ContainerTractorRepository containerTractorRepository;

  @Mock
  private SupplyRepository supplyRepository;

  PaginationRequest paginationRequest;

  Page<Inbound> pages;

  List<Inbound> inbounds;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    inbounds = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
  @DisplayName("Create inbound success")
  public void whenCreateInbound_thenReturnInbound() {
    // given

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLadingRequest billOfLadingRequest = new BillOfLadingRequest();
    billOfLadingRequest.setPortOfDelivery(port.getNameCode());
    billOfLadingRequest.setNumber("SE0101");
    billOfLadingRequest.setFreeTime("2020-07-26T15:41");
    billOfLadingRequest.setUnit(3);

    InboundRequest request = new InboundRequest();
    request.setCode("0001");
    request.setShippingLine(shippingLine.getCompanyCode());
    request.setContainerType(containerType.getName());
    request.setPickupTime("2020-07-25T10:05");
    request.setEmptyTime("2020-07-27T15:41");
    request.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    request.setBillOfLading(billOfLadingRequest);

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(inboundRepository.save(Mockito.any(Inbound.class))).thenReturn(inbound);
    // then
    Inbound actualResult = inboundServiceImpl.createInbound(forwarder.getUsername(), request);
    assertThat(actualResult).isNotNull();
  }

  @Test
  @DisplayName("Create inbound when pickupTime after freeTime")
  public void whenCreateInbound_Return500_time() {
    // given

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLadingRequest billOfLadingRequest = new BillOfLadingRequest();
    billOfLadingRequest.setPortOfDelivery(port.getNameCode());
    billOfLadingRequest.setNumber("SE0101");
    billOfLadingRequest.setFreeTime("2020-07-25T15:41");
    billOfLadingRequest.setUnit(3);

    InboundRequest request = new InboundRequest();
    request.setCode("0001");
    request.setShippingLine(shippingLine.getCompanyCode());
    request.setContainerType(containerType.getName());
    request.setPickupTime("2020-07-26T10:05");
    request.setEmptyTime("2020-07-27T15:41");
    request.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    request.setBillOfLading(billOfLadingRequest);

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      inboundServiceImpl.createInbound(forwarder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create inbound when shippingLine not found")
  public void whenCreateInbound_thenReturn404_shippingLine() {
    // given

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLadingRequest billOfLadingRequest = new BillOfLadingRequest();
    billOfLadingRequest.setPortOfDelivery(port.getNameCode());
    billOfLadingRequest.setNumber("SE0101");
    billOfLadingRequest.setFreeTime("2020-07-26T15:41");
    billOfLadingRequest.setUnit(3);

    InboundRequest request = new InboundRequest();
    request.setCode("0001");
    request.setContainerType(containerType.getName());
    request.setPickupTime("2020-07-25T10:05");
    request.setEmptyTime("2020-07-27T15:41");
    request.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    request.setBillOfLading(billOfLadingRequest);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.createInbound(forwarder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create inbound when containerType not found")
  public void whenCreateInbound_thenReturn404_containerType() {
    // given

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLadingRequest billOfLadingRequest = new BillOfLadingRequest();
    billOfLadingRequest.setPortOfDelivery(port.getNameCode());
    billOfLadingRequest.setNumber("SE0101");
    billOfLadingRequest.setFreeTime("2020-07-26T15:41");
    billOfLadingRequest.setUnit(3);

    InboundRequest request = new InboundRequest();
    request.setCode("0001");
    request.setShippingLine(shippingLine.getCompanyCode());
    request.setPickupTime("2020-07-25T10:05");
    request.setEmptyTime("2020-07-27T15:41");
    request.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    request.setBillOfLading(billOfLadingRequest);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.createInbound(forwarder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create inbound when Port not found")
  public void whenCreateInbound_thenReturn404_Port() {
    // given

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    BillOfLadingRequest billOfLadingRequest = new BillOfLadingRequest();
    billOfLadingRequest.setNumber("SE0101");
    billOfLadingRequest.setFreeTime("2020-07-26T15:41");
    billOfLadingRequest.setUnit(3);

    InboundRequest request = new InboundRequest();
    request.setCode("0001");
    request.setShippingLine(shippingLine.getCompanyCode());
    request.setContainerType(containerType.getName());
    request.setPickupTime("2020-07-25T10:05");
    request.setEmptyTime("2020-07-27T15:41");
    request.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    request.setBillOfLading(billOfLadingRequest);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.createInbound(forwarder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create inbound when Inbound Code exists")
  public void whenCreateInbound_thenReturn500_code() {
    // given

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLadingRequest billOfLadingRequest = new BillOfLadingRequest();
    billOfLadingRequest.setPortOfDelivery(port.getNameCode());
    billOfLadingRequest.setNumber("SE0101");
    billOfLadingRequest.setFreeTime("2020-07-26T15:41");
    billOfLadingRequest.setUnit(3);

    InboundRequest request = new InboundRequest();
    request.setCode("0001");
    request.setShippingLine(shippingLine.getCompanyCode());
    request.setContainerType(containerType.getName());
    request.setPickupTime("2020-07-25T10:05");
    request.setEmptyTime("2020-07-27T15:41");
    request.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    request.setBillOfLading(billOfLadingRequest);

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(true);
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      inboundServiceImpl.createInbound(forwarder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create inbound when BillOfLading number not found")
  public void whenCreateInbound_thenReturn404_number() {
    // given

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLadingRequest billOfLadingRequest = new BillOfLadingRequest();
    billOfLadingRequest.setPortOfDelivery(port.getNameCode());
    billOfLadingRequest.setFreeTime("2020-07-26T15:41");
    billOfLadingRequest.setUnit(3);

    InboundRequest request = new InboundRequest();
    request.setCode("0001");
    request.setShippingLine(shippingLine.getCompanyCode());
    request.setContainerType(containerType.getName());
    request.setPickupTime("2020-07-25T10:05");
    request.setEmptyTime("2020-07-27T15:41");
    request.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    request.setBillOfLading(billOfLadingRequest);

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(supplyRepository.existsByCode(Mockito.anyString())).thenReturn(false);
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.createInbound(forwarder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Create inbound when inbound code not found")
  public void whenCreateInbound_thenReturn404_code() {
    // given

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLadingRequest billOfLadingRequest = new BillOfLadingRequest();
    billOfLadingRequest.setNumber("SE0101");
    billOfLadingRequest.setPortOfDelivery(port.getNameCode());
    billOfLadingRequest.setFreeTime("2020-07-26T15:41");
    billOfLadingRequest.setUnit(3);

    InboundRequest request = new InboundRequest();
    request.setShippingLine(shippingLine.getCompanyCode());
    request.setContainerType(containerType.getName());
    request.setPickupTime("2020-07-25T10:05");
    request.setEmptyTime("2020-07-27T15:41");
    request.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    request.setBillOfLading(billOfLadingRequest);

    Inbound inbound = new Inbound();
    inbound.setId(1L);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.createInbound(forwarder.getUsername(), request);
    });
  }

  @Test
  @DisplayName("Get inbounds success")
  public void whenGetInbounds_thenReturnInboundPages() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setUnit(3);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setForwarder(forwarder);
    inbound.setCode("0001");
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);

    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);

    // when
    when(inboundRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Inbound> actualPages = inboundServiceImpl.getInbounds(paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getForwarder().getId()).isEqualTo(forwarder.getId());
  }

  @Test
  @DisplayName("Get inboundById success")
  public void whenGetInboundById_thenReturnInbound() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setUnit(3);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setForwarder(forwarder);
    inbound.setCode("0001");
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));

    // then
    Inbound actualResult = inboundServiceImpl.getInboundById(inbound.getId());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getForwarder().getId()).isEqualTo(forwarder.getId());
  }

  @Test
  @DisplayName("Get inboundById Not found")
  public void whenGetInboundById_thenReturn404() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setUnit(3);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setForwarder(forwarder);
    inbound.setCode("0001");
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.getInboundById(2L);
    });
  }

  @Test
  @DisplayName("Get inboundsByForwarder Success")
  public void whenGetInboundsByForwarder_thenReturnInbounds() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setUnit(3);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setForwarder(forwarder);
    inbound.setCode("0001");
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);

    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);

    // when
    when(inboundRepository.findByFowarder(Mockito.anyString(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Inbound> actualPages = inboundServiceImpl.getInboundsByForwarder(forwarder.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getForwarder().getId()).isEqualTo(forwarder.getId());
    assertThat(actualPages.getContent().get(0).getForwarder().getUsername()).isEqualTo(forwarder.getUsername());
  }

  @Test
  @DisplayName("Get inboundsByOutbound Success")
  public void whenGetInboundsByOutbound_thenReturnInbounds() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setForwarder(forwarder);
    inbound.setCode("0001");
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setBillOfLading(billOfLading);

    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(inboundRepository.findByOutbound(Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Page<Inbound> actualPages = inboundServiceImpl.getInboundsByOutbound(outbound.getId(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getContainerType().getName())
        .isEqualTo(outbound.getContainerType().getName());
    assertThat(actualPages.getContent().get(0).getShippingLine().getCompanyCode())
        .isEqualTo(outbound.getShippingLine().getCompanyCode());
  }

  @Test
  @DisplayName("Get inboundsByOutbound Notfound Exception")
  public void whenGetInboundsByOutbound_thenReturn404() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setForwarder(forwarder);
    inbound.setCode("0001");
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setBillOfLading(billOfLading);

    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    when(inboundRepository.findByOutbound(Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.getInboundsByOutbound(outbound.getId(), paginationRequest);
    });
  }

  @Test
  @DisplayName("Edit inbound success")
  public void whenEditInbound_thenReturnInbound() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("pickupTime", "2020-07-25T15:41");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(inboundRepository.save(Mockito.any(Inbound.class))).thenReturn(inbound);
    // then
    Inbound actualResult = inboundServiceImpl.editInbound(updates, inbound.getId(), forwarder.getUsername());
    assertThat(actualResult).isNotNull();
  }

  @Test
  @DisplayName("Edit inbound Notfound")
  public void whenEditInbound_thenReturn404_Inbound() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.editInbound(updates, inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit inbound User Access Denied")
  public void whenEditInbound_thenUserAccessDenied() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));
    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      inboundServiceImpl.editInbound(updates, inbound.getId(), "XXXX");
    });
  }

  @Test
  @DisplayName("Edit inbound when has Combined or BIDDING")
  public void whenEditInbound_thenReturnInboundIsInTransaction() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("COMBINED");

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      inboundServiceImpl.editInbound(updates, inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit inbound when shippingline not found")
  public void whenEditInbound_thenReturn404_Shippingline() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.editInbound(updates, inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit inbound when Containertype not found")
  public void whenEditInbound_thenReturn404_Containertype() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.editInbound(updates, inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit inbound when containerBusy")
  public void whenEditInbound_thenReturnContainerBusyException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("pickupTime", "2020-07-25T15:41");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(false);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      inboundServiceImpl.editInbound(updates, inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit inbound when DriverBusy")
  public void whenEditInbound_thenReturnDriverBusyException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("pickupTime", "2020-07-25T15:41");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(false);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      inboundServiceImpl.editInbound(updates, inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit inbound when TractorBusy")
  public void whenEditInbound_thenReturnTractorBusyException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("pickupTime", "2020-07-25T15:41");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(false);
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(inboundRepository.save(Mockito.any(Inbound.class))).thenReturn(inbound);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      inboundServiceImpl.editInbound(updates, inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit inbound when TrailerBusy")
  public void whenEditInbound_thenReturnTrailerBusyException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("pickupTime", "2020-07-25T15:41");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(false);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      inboundServiceImpl.editInbound(updates, inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit inbound when pickupTime after freetime")
  public void whenEditInbound_thenReturnInboundInvalidFreeTimeException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("shippingLine", "AL01");
    updates.put("containerType", "8CD");
    updates.put("pickupTime", "2020-07-28T15:41");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));
    when(shippingLineRepository.findByCompanyCode(Mockito.anyString())).thenReturn(Optional.of(shippingLine));
    when(containerTypeRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(containerType));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(inboundRepository.save(Mockito.any(Inbound.class))).thenReturn(inbound);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      inboundServiceImpl.editInbound(updates, inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Remove inbound success")
  public void whenRemoveInbound_Success() {
    // given

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));

    // then
    inboundServiceImpl.removeInbound(inbound.getId(), forwarder.getUsername());
  }

  @Test
  @DisplayName("Remove inbound Notfound")
  public void whenRemoveInbound_thenReturn404() {
    // given

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.removeInbound(inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Remove inbound User Access Denied")
  public void whenRemoveInbound_thenReturnUserAccessDeniedException() {
    // given

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      inboundServiceImpl.removeInbound(inbound.getId(), "XXXXX");
    });
  }

  @Test
  @DisplayName("Remove inbound when has container Combined or bidding")
  public void whenRemoveInbound_thenReturnContainerBusyException() {
    // given

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("COMBINED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(inbound));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      inboundServiceImpl.removeInbound(inbound.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("GetInboundsByOutboundAndForwarder success")
  public void whenGetInboundsByOutboundAndForwarder_thenReturnInbounds() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setForwarder(forwarder);
    inbound.setCode("0001");
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setBillOfLading(billOfLading);

    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(outbound));
    when(inboundRepository.findByOutboundAndForwarder(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Inbound> actualPages = inboundServiceImpl.getInboundsByOutboundAndForwarder(outbound.getId(),
        forwarder.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getContainerType().getName())
        .isEqualTo(outbound.getContainerType().getName());
    assertThat(actualPages.getContent().get(0).getShippingLine().getCompanyCode())
        .isEqualTo(outbound.getShippingLine().getCompanyCode());
    assertThat(actualPages.getContent().get(0).getForwarder().getUsername()).isEqualTo(forwarder.getUsername());
  }

  @Test
  @DisplayName("GetInboundsByOutboundAndForwarder Notfound Exception")
  public void whenGetInboundsByOutboundAndForwarder_thenReturn404() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setUnit(3);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setShippingLine(shippingLine);
    outbound.setContainerType(containerType);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setForwarder(forwarder);
    inbound.setCode("0001");
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setBillOfLading(billOfLading);

    inbounds.add(inbound);
    pages = new PageImpl<Inbound>(inbounds);

    // when
    when(outboundRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.getInboundsByOutboundAndForwarder(outbound.getId(), forwarder.getUsername(),
          paginationRequest);
    });
  }

  @Test
  @DisplayName("Get inboundByContainer success")
  public void whenGetInboundByContainer_thenReturnInbound() {
    // given
    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("COMBINED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findInboundByContainer(Mockito.anyLong())).thenReturn(Optional.of(inbound));

    // then
    Inbound actualResult = inboundServiceImpl.getInboundByContainer(container.getId());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getForwarder().getId()).isEqualTo(forwarder.getId());
  }

  @Test
  @DisplayName("Get inboundByContainer Not found")
  public void whenGetInboundByContainer_thenReturn404() {
    // given
    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setId(1L);
    shippingLine.setCompanyCode("APL");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("COMBINED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containers);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setCode("0001");
    inbound.setShippingLine(shippingLine);
    inbound.setContainerType(containerType);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setEmptyTime(Tool.convertToLocalDateTime("2020-07-27T15:41"));
    inbound.setReturnStation("Thành phố Nha Trang Khánh Hòa Việt Nam");
    inbound.setBillOfLading(billOfLading);
    inbound.setForwarder(forwarder);

    // when
    when(inboundRepository.findInboundByContainer(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      inboundServiceImpl.getInboundByContainer(2L);
    });
  }
}
