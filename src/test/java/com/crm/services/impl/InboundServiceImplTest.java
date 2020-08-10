package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

import com.crm.exception.DuplicateRecordException;
import com.crm.exception.NotFoundException;
import com.crm.models.ContainerType;
import com.crm.models.Forwarder;
import com.crm.models.Inbound;
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

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

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
    when(billOfLadingRepository.existsByNumber(Mockito.anyString())).thenReturn(false);
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(inboundRepository.save(Mockito.any(Inbound.class))).thenReturn(inbound);
    // then
    Inbound actualResult = inboundServiceImpl.createInbound(forwarder.getUsername(), request);
    assertThat(actualResult).isNotNull();
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
    when(billOfLadingRepository.existsByNumber(Mockito.anyString())).thenReturn(false);
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
  @DisplayName("Create inbound when BillOfLading number exists")
  public void whenCreateInbound_thenReturn500_number() {
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
    when(billOfLadingRepository.existsByNumber(Mockito.anyString())).thenReturn(true);
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      inboundServiceImpl.createInbound(forwarder.getUsername(), request);
    });
  }
}
