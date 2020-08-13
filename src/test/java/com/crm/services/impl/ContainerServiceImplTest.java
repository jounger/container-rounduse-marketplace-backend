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
import com.crm.models.Bid;
import com.crm.models.BillOfLading;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.ContainerTractor;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Inbound;
import com.crm.models.Port;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BillOfLadingRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerSemiTrailerRepository;
import com.crm.repository.ContainerTractorRepository;
import com.crm.repository.DriverRepository;

public class ContainerServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(ContainerServiceImplTest.class);

  @InjectMocks
  ContainerServiceImpl containerServiceImpl;

  @Mock
  private ContainerRepository containerRepository;

  @Mock
  private DriverRepository driverRepository;

  @Mock
  private BillOfLadingRepository billOfLadingRepository;

  @Mock
  private ContainerSemiTrailerRepository containerSemiTrailerRepository;

  @Mock
  private ContainerTractorRepository containerTractorRepository;

  @Mock
  private BidRepository bidRepository;

  PaginationRequest paginationRequest;

  PaginationRequest paginationRequestHasStatus;

  Page<Container> pages;

  List<Container> containers;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    containers = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);

    paginationRequestHasStatus = new PaginationRequest();
    paginationRequestHasStatus.setPage(0);
    paginationRequestHasStatus.setLimit(10);
    paginationRequestHasStatus.setStatus("CREATED");
  }

  @Test
  @DisplayName("Get ContainersByInbound success")
  public void whenGetContainersByInbound_thenReturnContainers() {

    // given

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

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

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
    container.setBillOfLading(billOfLading);

    containers.add(container);
    pages = new PageImpl<Container>(containers);

    // when
    when(containerRepository.findContainersByInbound(Mockito.anyLong(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Page<Container> actualPages = containerServiceImpl.getContainersByInbound(inbound.getId(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getBillOfLading().getId()).isEqualTo(billOfLading.getId());
  }

  @Test
  @DisplayName("Get Containers success")
  public void whenGetContainers_thenReturnContainers() {

    // given

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

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

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
    container.setBillOfLading(billOfLading);

    containers.add(container);
    pages = new PageImpl<Container>(containers);

    // when
    when(containerRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Container> actualPages = containerServiceImpl.getContainers(paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getBillOfLading().getId()).isEqualTo(billOfLading.getId());
  }

  @Test
  @DisplayName("Get ContainerById success")
  public void whenGetContainerById_thenReturnContainer() {

    // given

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

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

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
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));

    // then
    Container actualResult = containerServiceImpl.getContainerById(container.getId());
    logger.info("actualPages: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getBillOfLading().getId()).isEqualTo(billOfLading.getId());
  }

  @Test
  @DisplayName("Get ContainerById when container NotFound")
  public void whenGetContainerById_thenReturnContainerNotFoundException() {

    // given

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

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

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
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerServiceImpl.getContainerById(2L);
    });
  }

  @Test
  @DisplayName("Get ContainersByBillOfLading success")
  public void whenGetContainersByBillOfLading_thenReturnContainers() {

    // given

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

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

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
    container.setBillOfLading(billOfLading);

    containers.add(container);
    pages = new PageImpl<Container>(containers);

    // when
    when(containerRepository.findByBillOfLading(Mockito.anyLong(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Container> actualPages = containerServiceImpl.getContainersByBillOfLading(billOfLading.getId(),
        paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getBillOfLading().getId()).isEqualTo(billOfLading.getId());
  }

  @Test
  @DisplayName("Create Container success")
  public void whenCreateContainer_thenReturnContainer() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(true);

    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(tractor));

    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class))).thenReturn(true);

    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class))).thenReturn(true);

    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class))).thenReturn(true);

    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(container);

    // then
    Container actualResult = containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(),
        containerRequest);
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(container.getId());
  }

  @Test
  @DisplayName("Create Container when billOfLadingNotfound")
  public void whenCreateContainer_thenReturnNotfoundException_billOfLading() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Container more than need")
  public void whenCreateContainer_thenReturnContainerMoreThanNeedException() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(1);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Container exists")
  public void whenCreateContainer_thenReturnContainerAlreadyExistsException() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S001");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR001");
    containerRequest.setTractor("TRT001");
    containerRequest.setTrailer("TRL001");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Driver exists")
  public void whenCreateContainer_thenReturnDriverAlreadyExistsException() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("UserName");
    containerRequest.setTractor("TRT001");
    containerRequest.setTrailer("TRL001");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Trailer exists")
  public void whenCreateContainer_thenReturnTrailerAlreadyExistsException() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR001");
    containerRequest.setTractor("TRT001");
    containerRequest.setTrailer("012345");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Tractor exists")
  public void whenCreateContainer_thenReturnTractorAlreadyExistsException() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR001");
    containerRequest.setTractor("54365");
    containerRequest.setTrailer("TRL001");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));
    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Container Busy")
  public void whenCreateContainer_thenReturnContainerBusyException() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(false);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Driver NotFound")
  public void whenCreateContainer_thenReturnNotFoundException_Driver() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(true);

    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Access Denied Driver")
  public void whenCreateContainer_thenReturnAccessDeniedException_Driver() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder2);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(true);

    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Trailer NotFound")
  public void whenCreateContainer_thenReturnNotFoundException_Trailer() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(true);

    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Access Denied Trailer")
  public void whenCreateContainer_thenReturnAccessDeniedException_Trailer() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder2);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(true);

    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Tractor NotFound")
  public void whenCreateContainer_thenReturnNotFoundException_Tractor() {

    // given
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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(true);

    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Access Denied Tractor")
  public void whenCreateContainer_thenReturnAccessDeniedException_Tractor() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder2);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(true);

    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(tractor));
    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Driver Busy")
  public void whenCreateContainer_thenReturnDriverBusyException() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(true);

    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(tractor));

    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class))).thenReturn(false);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Tractor Busy")
  public void whenCreateContainer_thenReturnTractorBusyException() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(true);

    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(tractor));

    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class))).thenReturn(true);

    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class))).thenReturn(false);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Create Container when Trailer Busy")
  public void whenCreateContainer_thenReturnTrailerBusyException() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);

    Collection<Container> containerCollection = new ArrayList<>();
    containerCollection.add(container);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setContainers(containerCollection);
    billOfLading.setInbound(inbound);

    ContainerRequest containerRequest = new ContainerRequest();
    containerRequest.setNumber("S002");
    containerRequest.setStatus("CREATED");
    containerRequest.setDriver("DR1");
    containerRequest.setTractor("TRT1");
    containerRequest.setTrailer("TRL1");

    // when
    when(billOfLadingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(billOfLading));

    when(containerRepository.findByNumber(Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(true);

    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(tractor));

    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class))).thenReturn(true);

    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class))).thenReturn(true);

    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class))).thenReturn(false);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.createContainer(billOfLading.getId(), forwarder.getUsername(), containerRequest);
    });
  }

  @Test
  @DisplayName("Edit Container success")
  public void whenEditContainer_thenReturnContainer() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "BIDDING");

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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(tractor));
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(container);

    // then
    Container actualResult = containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(container.getId());
  }

  @Test
  @DisplayName("Edit Container when container NotFound")
  public void whenEditContainer_thenReturnNotFoundException_Container() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "BIDDING");

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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when container Access Denied")
  public void whenEditContainer_thenReturnAccessDeniedException_Container() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "BIDDING");

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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), "XXXX");
    });
  }

  @Test
  @DisplayName("Edit Container when container BIDDING")
  public void whenEditContainer_thenReturnContainerBiddingException() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "CREATED");

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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("BIDDING");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when container COMBINED")
  public void whenEditContainer_thenReturnContainerCombinedException() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "CREATED");

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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("COMBINED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when driver NotFound")
  public void whenEditContainer_thenReturnNotFoundException_Driver() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "COMBINED");

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

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when driver Access Denied")
  public void whenEditContainer_thenReturnAccessDeniedException_Driver() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "COMBINED");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder2);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when Driver Busy")
  public void whenEditContainer_thenReturnDriverBusyException() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "COMBINED");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(false);
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(tractor));
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(container);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when Trailer NotFound")
  public void whenEditContainer_thenReturnNotFoundException_Trailer() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "COMBINED");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.empty());
    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when Trailer Access Denied")
  public void whenEditContainer_thenReturnAccessDeniedException_Trailer() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "COMBINED");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder2);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when Trailer Busy")
  public void whenEditContainer_thenReturnTrailerBusyException() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "COMBINED");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(false);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when Tractor NotFound")
  public void whenEditContainer_thenReturnNotFoundException_Tractor() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "COMBINED");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when Tractor Access Denied")
  public void whenEditContainer_thenReturnAccessDeniedException_Tractor() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "COMBINED");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder2);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(tractor));
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(true);
    when(containerRepository.save(Mockito.any(Container.class))).thenReturn(container);

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when Tractor Busy")
  public void whenEditContainer_thenReturnTractorBusyException() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "COMBINED");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(tractor));
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(false);
    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Container when Container Busy")
  public void whenEditContainer_thenReturnContainerBusyException() {

    // given

    Map<String, Object> updates = new HashMap<>();
    updates.put("containerNumber", "AL01");
    updates.put("driver", "driver");
    updates.put("trailer", "TRL0001");
    updates.put("tractor", "TRT0001");
    updates.put("status", "COMBINED");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Forwarder forwarder2 = new Forwarder();
    forwarder2.setId(2L);
    forwarder2.setUsername("forwarder2");

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setPickupTime(Tool.convertToLocalDateTime("2020-07-25T10:05"));
    inbound.setForwarder(forwarder);

    Port port = new Port();
    port.setId(1L);
    port.setNameCode("HHP");

    Driver driver = new Driver();
    driver.setId(1L);
    driver.setUsername("UserName");
    driver.setForwarder(forwarder);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);
    tractor.setLicensePlate("54365");
    tractor.setForwarder(forwarder);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(1L);
    trailer.setLicensePlate("012345");
    trailer.setForwarder(forwarder);

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

    Container container = new Container();
    container.setId(1L);
    container.setNumber("S001");
    container.setStatus("CREATED");
    container.setDriver(driver);
    container.setTractor(tractor);
    container.setTrailer(trailer);
    container.setBillOfLading(billOfLading);

    // when
    when(containerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(container));
    when(driverRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(driver));
    when(containerRepository.findByDriver(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(trailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerTractorRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.of(tractor));
    when(containerRepository.findByTractor(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class),
        Mockito.any(LocalDateTime.class), Mockito.anyLong())).thenReturn(true);
    when(containerRepository.findByNumber(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class))).thenReturn(false);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerServiceImpl.editContainer(updates, container.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Get ContainersByBid success")
  public void whenGetContainersByBid_thenReturnContainers() {

    // given

    Bid bid = new Bid();
    bid.setId(1L);

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

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

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
    container.setBillOfLading(billOfLading);

    containers.add(container);
    pages = new PageImpl<Container>(containers);

    // when
    when(containerRepository.findByBid(Mockito.anyLong(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Container> actualPages = containerServiceImpl.getContainersByBid(bid.getId(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getBillOfLading().getId()).isEqualTo(billOfLading.getId());
  }

  @Test
  @DisplayName("Get ContainersByBid success")
  public void whenGetContainersByBidAndStatus_thenReturnContainers() {

    // given

    Bid bid = new Bid();
    bid.setId(1L);

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

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

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
    container.setBillOfLading(billOfLading);

    containers.add(container);
    pages = new PageImpl<Container>(containers);

    // when
    when(containerRepository.findByBid(Mockito.anyLong(), Mockito.anyString(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Page<Container> actualPages = containerServiceImpl.getContainersByBid(bid.getId(), paginationRequestHasStatus);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getBillOfLading().getId()).isEqualTo(billOfLading.getId());
  }

  @Test
  @DisplayName("Get ListContainersByBidAndStatus success")
  public void whenGetListContainersByBidAndStatus_thenReturnContainers() {

    // given

    Bid bid = new Bid();
    bid.setId(1L);

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

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.setNumber("SE0101");
    billOfLading.setFreeTime(Tool.convertToLocalDateTime("2020-07-26T15:41"));
    billOfLading.setUnit(3);
    billOfLading.setInbound(inbound);

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
    container.setBillOfLading(billOfLading);

    containers = new ArrayList<Container>();
    containers.add(container);

    // when
    when(containerRepository.findByBidAndStatus(Mockito.anyLong(), Mockito.anyString())).thenReturn(containers);

    // then
    List<Container> actualPages = containerServiceImpl.getContainersByBidAndStatus(bid.getId(),
        paginationRequestHasStatus.getStatus());
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.size()).isEqualTo(1);
    assertThat(actualPages.get(0).getBillOfLading().getId()).isEqualTo(billOfLading.getId());
  }
}
