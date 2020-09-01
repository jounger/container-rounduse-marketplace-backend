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

import com.crm.common.Tool;
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
import com.crm.models.Port;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.InboundRepository;
import com.crm.repository.PortRepository;

public class BillOfLadingServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(BillOfLadingServiceImplTest.class);

  @InjectMocks
  BillOfLadingServiceImpl billOfLadingServiceImpl;

  @Mock
  BillOfLadingRepository billOfLadingRepository;

  @Mock
  PortRepository portRepository;

  @Mock
  InboundRepository inboundRepository;

  @Mock
  private ContainerRepository containerRepository;

  PaginationRequest paginationRequest;

  PaginationRequest paginationRequestHasStatus;

  Page<BillOfLading> pages;

  List<BillOfLading> billOfLadings;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    billOfLadings = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);

    paginationRequestHasStatus = new PaginationRequest();
    paginationRequestHasStatus.setPage(0);
    paginationRequestHasStatus.setLimit(10);
    paginationRequestHasStatus.setStatus("CREATED");
  }

  @Test
  @DisplayName("Get BillOfLadingByInbound success")
  public void whenGetBillOfLadingByInbound_thenReturnBillOfLading() {
    // given
    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    // when
    when(inboundRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(billOfLadingRepository.findByInbound(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    // then
    BillOfLading actualResult = billOfLadingServiceImpl.getBillOfLadingByInbound(billOfLading.getId());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getInbound().getId()).isEqualTo(inbound.getId());
  }

  @Test
  @DisplayName("Get BillOfLadingByInbound when inbound not found")
  public void whenGetBillOfLadingByInbound_thenReturnNotfoundException_Inbound() {
    // given
    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    // when
    when(inboundRepository.existsById(Mockito.anyLong())).thenReturn(false);
    when(billOfLadingRepository.findByInbound(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      billOfLadingServiceImpl.getBillOfLadingByInbound(2L);
    });
  }

  @Test
  @DisplayName("Get BillOfLadingByInbound when BillOfLading not found")
  public void whenGetBillOfLadingByInbound_thenReturnNotfoundException_BillOfLading() {
    // given
    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    // when
    when(inboundRepository.existsById(Mockito.anyLong())).thenReturn(true);
    when(billOfLadingRepository.findByInbound(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      billOfLadingServiceImpl.getBillOfLadingByInbound(inbound.getId());
    });
  }

  @Test
  @DisplayName("Get getBillOfLadingByNumber success")
  public void whenGetBillOfLadingByNumber_thenReturnBillOfLading() {
    // given
    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findByNumber(Mockito.anyString())).thenReturn(Optional.of(billOfLading));

    // then
    BillOfLading actualResult = billOfLadingServiceImpl.getBillOfLadingByNumber(billOfLading.getNumber());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(billOfLading.getId());
    assertThat(actualResult.getInbound().getId()).isEqualTo(inbound.getId());
  }

  @Test
  @DisplayName("Get getBillOfLadingByNumber when BillOfLading not found")
  public void whenGetBillOfLadingByNumber_thenReturnNotfoundException_BillOfLading() {
    // given
    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findByNumber(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      billOfLadingServiceImpl.getBillOfLadingByNumber(billOfLading.getNumber());
    });
  }

  @Test
  @DisplayName("Get getBillOfLadingById success")
  public void whenGetBillOfLadingById_thenReturnBillOfLading() {
    // given
    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    // then
    BillOfLading actualResult = billOfLadingServiceImpl.getBillOfLadingById(billOfLading.getId());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(billOfLading.getId());
    assertThat(actualResult.getInbound().getId()).isEqualTo(inbound.getId());
  }

  @Test
  @DisplayName("Get getBillOfLadingById when BillOfLading not found")
  public void whenGetBillOfLadingById_thenReturnNotfoundException_BillOfLading() {
    // given
    Inbound inbound = new Inbound();
    inbound.setId(1L);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      billOfLadingServiceImpl.getBillOfLadingById(billOfLading.getId());
    });
  }

  @Test
  @DisplayName("Edit BillOfLading success")
  public void whenEditBillOfLading_thenReturnBillOfLading() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "5");
    updates.put("freeTime", "2020-07-25T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(billOfLadingRepository.save(Mockito.any(BillOfLading.class))).thenReturn(billOfLading);

    // then
    BillOfLading actualResult = billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(),
        forwarder.getUsername());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(billOfLading.getId());
    assertThat(actualResult.getInbound().getId()).isEqualTo(inbound.getId());
  }

  @Test
  @DisplayName("Edit BillOfLading when BillOfLading NotFound")
  public void whenEditBillOfLading_thenReturnNotFoundException_BillOfLading() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "5");
    updates.put("freeTime", "2020-07-25T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit BillOfLading when Access Denied")
  public void whenEditBillOfLading_thenReturnAccessDeniedException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "5");
    updates.put("freeTime", "2020-07-25T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(), "XXXXX");
    });
  }

  @Test
  @DisplayName("Edit BillOfLading when Port Not Found")
  public void whenEditBillOfLading_thenReturnNotFoundException_Port() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "5");
    updates.put("freeTime", "2020-07-25T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit BillOfLading when Unit less than need")
  public void whenEditBillOfLading_thenReturnUnitLessThanNeedException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "0");
    updates.put("freeTime", "2020-07-25T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit BillOfLading when Container Combined Or Bidding")
  public void whenEditBillOfLading_thenReturnContainerCombinedOrBiddingException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "5");
    updates.put("freeTime", "2020-07-25T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit BillOfLading when Container Busy")
  public void whenEditBillOfLading_thenReturnContainerBusyException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "5");
    updates.put("freeTime", "2020-07-25T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(false);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit BillOfLading when Driver Busy")
  public void whenEditBillOfLading_thenReturnDriverBusyException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "5");
    updates.put("freeTime", "2020-07-25T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(false);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit BillOfLading when ContainerTractor Busy")
  public void whenEditBillOfLading_thenReturnContainerTractorBusyException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "5");
    updates.put("freeTime", "2020-07-25T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(false);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit BillOfLading when ContainerSemiTrailer Busy")
  public void whenEditBillOfLading_thenReturnContainerSemiTrailerBusyException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "5");
    updates.put("freeTime", "2020-07-25T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
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
      billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(), forwarder.getUsername());
    });
  }
  
  @Test
  @DisplayName("Edit BillOfLading when PickupTime after freeTime")
  public void whenEditBillOfLading_thenReturnInvalidFreeTimeException() {
    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("portOfDelivery", "AL01");
    updates.put("number", "8CD");
    updates.put("unit", "5");
    updates.put("freeTime", "2020-07-24T15:41");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-26T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    ContainerType containerType = new ContainerType();
    containerType.setId(1L);
    containerType.setName("10CD");

    Driver driver = new Driver();
    driver.setId(1L);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);

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
    billOfLading.setInbound(inbound);

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    when(portRepository.findByNameCode(Mockito.anyString())).thenReturn(Optional.of(port));
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      billOfLadingServiceImpl.editBillOfLading(updates, billOfLading.getId(), forwarder.getUsername());
    });
  }
}
