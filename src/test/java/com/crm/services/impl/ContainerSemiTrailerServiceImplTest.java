package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

import com.crm.enums.EnumSupplyStatus;
import com.crm.enums.EnumTrailerType;
import com.crm.enums.EnumUnit;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.Forwarder;
import com.crm.payload.request.ContainerSemiTrailerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ContainerSemiTrailerRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.VehicleRepository;

public class ContainerSemiTrailerServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(ContainerSemiTrailerServiceImplTest.class);

  @InjectMocks
  ContainerSemiTrailerServiceImpl containerSemiTrailerServiceImpl;

  @Mock
  ContainerSemiTrailerRepository containerSemiTrailerRepository;

  @Mock
  VehicleRepository vehicleRepository;

  @Mock
  ForwarderRepository forwarderRepository;

  @Mock
  ContainerRepository containerRepository;

  PaginationRequest paginationRequest;

  List<ContainerSemiTrailer> containerSemiTrailers;

  Page<ContainerSemiTrailer> pages;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    containerSemiTrailers = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
  @DisplayName("Get ContainerSemiTrailer Success")
  public void whenGetContainerSemiTrailer_thenReturnContainerSemiTrailer() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));

    // then
    ContainerSemiTrailer actualResult = containerSemiTrailerServiceImpl
        .getContainerSemiTrailerById(containerSemiTrailer.getId());
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getType()).isEqualTo(EnumTrailerType.T28.name());
  }

  @Test
  @DisplayName("Get ContainerSemiTrailer NotFound")
  public void whenGetContainerSemiTrailer_thenReturnNotFoundException_ContainerSemiTrailer() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerSemiTrailerServiceImpl.getContainerSemiTrailerById(containerSemiTrailer.getId());
    });
  }

  @Test
  @DisplayName("Get ContainerSemiTrailers Success")
  public void whenGetContainerSemiTrailers_thenReturnContainerSemiTrailers() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setForwarder(forwarder);

    containerSemiTrailers.add(containerSemiTrailer);
    pages = new PageImpl<>(containerSemiTrailers);

    // when
    when(containerSemiTrailerRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<ContainerSemiTrailer> actualPages = containerSemiTrailerServiceImpl
        .getContainerSemiTrailers(paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getType()).isEqualTo(EnumTrailerType.T28.name());
  }

  @Test
  @DisplayName("Create ContainerSemiTrailer Success")
  public void whenCreateContainerSemiTrailer_thenReturnContainerSemiTrailer() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());

    ContainerSemiTrailerRequest containerSemiTrailerRequest = new ContainerSemiTrailerRequest();
    containerSemiTrailerRequest.setType(EnumTrailerType.T28.name());
    containerSemiTrailerRequest.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailerRequest.setLicensePlate("0123456");
    containerSemiTrailerRequest.setNumberOfAxles(1);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(vehicleRepository.existsByLicensePlate(Mockito.anyString())).thenReturn(false);
    when(containerSemiTrailerRepository.save(Mockito.any(ContainerSemiTrailer.class))).thenReturn(containerSemiTrailer);

    // then
    ContainerSemiTrailer actualResult = containerSemiTrailerServiceImpl
        .createContainerSemiTrailer(forwarder.getUsername(), containerSemiTrailerRequest);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getType()).isEqualTo(EnumTrailerType.T28.name());
  }

  @Test
  @DisplayName("Create ContainerSemiTrailer when forwarder notFound")
  public void whenCreateContainerSemiTrailer_thenReturnNotFoundException_ContainerSemiTrailer() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());

    ContainerSemiTrailerRequest containerSemiTrailerRequest = new ContainerSemiTrailerRequest();
    containerSemiTrailerRequest.setType(EnumTrailerType.T28.name());
    containerSemiTrailerRequest.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailerRequest.setLicensePlate("0123456");
    containerSemiTrailerRequest.setNumberOfAxles(1);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerSemiTrailerServiceImpl.createContainerSemiTrailer(forwarder.getUsername(), containerSemiTrailerRequest);
    });
  }

  @Test
  @DisplayName("Create ContainerSemiTrailer when Vehicle exist")
  public void whenCreateContainerSemiTrailer_thenReturnDuplicateRecord_ContainerSemiTrailer() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());

    ContainerSemiTrailerRequest containerSemiTrailerRequest = new ContainerSemiTrailerRequest();
    containerSemiTrailerRequest.setType(EnumTrailerType.T28.name());
    containerSemiTrailerRequest.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailerRequest.setLicensePlate("0123456");
    containerSemiTrailerRequest.setNumberOfAxles(1);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(vehicleRepository.existsByLicensePlate(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      containerSemiTrailerServiceImpl.createContainerSemiTrailer(forwarder.getUsername(), containerSemiTrailerRequest);
    });
  }

  @Test
  @DisplayName("Create ContainerSemiTrailer when TrailerType notFound")
  public void whenCreateContainerSemiTrailer_thenReturnNotFoundException_TrailerType() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());

    ContainerSemiTrailerRequest containerSemiTrailerRequest = new ContainerSemiTrailerRequest();
    containerSemiTrailerRequest.setType("XXXXX");
    containerSemiTrailerRequest.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailerRequest.setLicensePlate("0123456");
    containerSemiTrailerRequest.setNumberOfAxles(1);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(vehicleRepository.existsByLicensePlate(Mockito.anyString())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerSemiTrailerServiceImpl.createContainerSemiTrailer(forwarder.getUsername(), containerSemiTrailerRequest);
    });
  }

  @Test
  @DisplayName("Create ContainerSemiTrailer when UnitOfMeasurement notFound")
  public void whenCreateContainerSemiTrailer_thenReturnNotFoundException_UnitOfMeasurement() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());

    ContainerSemiTrailerRequest containerSemiTrailerRequest = new ContainerSemiTrailerRequest();
    containerSemiTrailerRequest.setType(EnumTrailerType.T28.name());
    containerSemiTrailerRequest.setUnitOfMeasurement("XXXXX");
    containerSemiTrailerRequest.setLicensePlate("0123456");
    containerSemiTrailerRequest.setNumberOfAxles(1);

    // when
    when(forwarderRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(forwarder));
    when(vehicleRepository.existsByLicensePlate(Mockito.anyString())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerSemiTrailerServiceImpl.createContainerSemiTrailer(forwarder.getUsername(), containerSemiTrailerRequest);
    });
  }

  @Test
  @DisplayName("Edit ContainerSemiTrailer Success")
  public void whenEditContainerSemiTrailer_thenReturnContainerSemiTrailer() {

    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("licensePlate", "020202");
    updates.put("numberOfAxles", 2);
    updates.put("type", EnumTrailerType.T32.name());
    updates.put("unitOfMeasurement", EnumUnit.FT.name());

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(containers);
    when(vehicleRepository.existsByLicensePlate(Mockito.anyString())).thenReturn(false);
    when(containerSemiTrailerRepository.save(Mockito.any(ContainerSemiTrailer.class))).thenReturn(containerSemiTrailer);

    // then
    ContainerSemiTrailer actualResult = containerSemiTrailerServiceImpl.editContainerSemiTrailer(updates,
        containerSemiTrailer.getId(), forwarder.getUsername());
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getType()).isEqualTo(EnumTrailerType.T32.name());
  }

  @Test
  @DisplayName("Edit ContainerSemiTrailer when ContainerSemiTrailer notFound")
  public void whenEditContainerSemiTrailer_thenReturnNotFoundException_ContainerSemiTrailer() {

    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("licensePlate", "020202");
    updates.put("numberOfAxles", 2);
    updates.put("type", EnumTrailerType.T32.name());
    updates.put("unitOfMeasurement", EnumUnit.FT.name());

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerSemiTrailerServiceImpl.editContainerSemiTrailer(updates, containerSemiTrailer.getId(),
          forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit ContainerSemiTrailer when ContainerSemiTrailer AccessDenied")
  public void whenEditContainerSemiTrailer_thenReturnAccessDeniedException_ContainerSemiTrailer() {

    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("licensePlate", "020202");
    updates.put("numberOfAxles", 2);
    updates.put("type", EnumTrailerType.T32.name());
    updates.put("unitOfMeasurement", EnumUnit.FT.name());

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      containerSemiTrailerServiceImpl.editContainerSemiTrailer(updates, containerSemiTrailer.getId(), "XXXX");
    });
  }

  @Test
  @DisplayName("Edit ContainerSemiTrailer when ContainerSemiTrailer BIDDING")
  public void whenEditContainerSemiTrailer_thenReturnContainerSemiTrailerBiddingException() {

    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("licensePlate", "020202");
    updates.put("numberOfAxles", 2);
    updates.put("type", EnumTrailerType.T32.name());
    updates.put("unitOfMeasurement", EnumUnit.FT.name());

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.BIDDING.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(containers);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerSemiTrailerServiceImpl.editContainerSemiTrailer(updates, containerSemiTrailer.getId(),
          forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit ContainerSemiTrailer when ContainerSemiTrailer COMBINED")
  public void whenEditContainerSemiTrailer_thenReturnContainerSemiTrailerCombinedException() {

    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("licensePlate", "020202");
    updates.put("numberOfAxles", 2);
    updates.put("type", EnumTrailerType.T32.name());
    updates.put("unitOfMeasurement", EnumUnit.FT.name());

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.COMBINED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(containers);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerSemiTrailerServiceImpl.editContainerSemiTrailer(updates, containerSemiTrailer.getId(),
          forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit ContainerSemiTrailer when licensePlate Exists")
  public void whenEditContainerSemiTrailer_thenReturnDuplicateRecordExceptionException_LicensePlate() {

    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("licensePlate", "020202");
    updates.put("numberOfAxles", 2);
    updates.put("type", EnumTrailerType.T32.name());
    updates.put("unitOfMeasurement", EnumUnit.FT.name());

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(containers);
    when(vehicleRepository.existsByLicensePlate(Mockito.anyString())).thenReturn(true);

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      containerSemiTrailerServiceImpl.editContainerSemiTrailer(updates, containerSemiTrailer.getId(),
          forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit ContainerSemiTrailer when Type NotFound")
  public void whenEditContainerSemiTrailer_thenReturnNotFoundException_Type() {

    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("licensePlate", "020202");
    updates.put("numberOfAxles", 2);
    updates.put("type", "XXXXX");
    updates.put("unitOfMeasurement", EnumUnit.FT.name());

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(containers);
    when(vehicleRepository.existsByLicensePlate(Mockito.anyString())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerSemiTrailerServiceImpl.editContainerSemiTrailer(updates, containerSemiTrailer.getId(),
          forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Edit ContainerSemiTrailer when UnitOfMeasurement NotFound")
  public void whenEditContainerSemiTrailer_thenReturnNotFoundException_UnitOfMeasurement() {

    // given
    Map<String, Object> updates = new HashMap<>();
    updates.put("licensePlate", "020202");
    updates.put("numberOfAxles", 2);
    updates.put("type", EnumTrailerType.T32.name());
    updates.put("unitOfMeasurement", "XXXX");

    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(containers);
    when(vehicleRepository.existsByLicensePlate(Mockito.anyString())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerSemiTrailerServiceImpl.editContainerSemiTrailer(updates, containerSemiTrailer.getId(),
          forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Remove ContainerSemiTrailer Success")
  public void whenRemoveContainerSemiTrailer_thenReturnSuccess() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(containers);

    // then
    containerSemiTrailerServiceImpl.removeContainerSemiTrailer(containerSemiTrailer.getId(), forwarder.getUsername());
  }

  @Test
  @DisplayName("Remove ContainerSemiTrailer When containerSemiTrailer NotFound")
  public void whenRemoveContainerSemiTrailer_thenReturnNotFoundException_ContainerSemiTrailer() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerSemiTrailerServiceImpl.removeContainerSemiTrailer(containerSemiTrailer.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Remove ContainerSemiTrailer when Access Denied")
  public void whenRemoveContainerSemiTrailer_thenReturnAccessDeniedException() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.CREATED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      containerSemiTrailerServiceImpl.removeContainerSemiTrailer(containerSemiTrailer.getId(), "XXXX");
    });
  }

  @Test
  @DisplayName("Remove ContainerSemiTrailer when Bidding")
  public void whenRemoveContainerSemiTrailer_thenReturnBiddingException() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.BIDDING.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(containers);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerSemiTrailerServiceImpl.removeContainerSemiTrailer(containerSemiTrailer.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Remove ContainerSemiTrailer when COMBINED")
  public void whenRemoveContainerSemiTrailer_thenReturnCOMBINEDException() {

    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("forwarder");

    Container container = new Container();
    container.setId(1L);
    container.setStatus(EnumSupplyStatus.COMBINED.name());

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setLicensePlate("0123456");
    containerSemiTrailer.setNumberOfAxles(1);
    containerSemiTrailer.setContainers(containers);
    containerSemiTrailer.setForwarder(forwarder);

    // when
    when(containerSemiTrailerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(containerSemiTrailer));
    when(containerRepository.findByTrailer(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(containers);

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      containerSemiTrailerServiceImpl.removeContainerSemiTrailer(containerSemiTrailer.getId(), forwarder.getUsername());
    });
  }

  @Test
  @DisplayName("Get ContainerSemiTrailersByForwarder Success")
  public void whenGetContainerSemiTrailersByForwarder_thenReturnContainerSemiTrailers() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setForwarder(forwarder);

    containerSemiTrailers.add(containerSemiTrailer);
    pages = new PageImpl<>(containerSemiTrailers);

    // when
    when(containerSemiTrailerRepository.findByForwarder(Mockito.anyString(), Mockito.any(PageRequest.class)))
        .thenReturn(pages);

    // then
    Page<ContainerSemiTrailer> actualPages = containerSemiTrailerServiceImpl
        .getContainerSemiTrailersByForwarder(forwarder.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getType()).isEqualTo(EnumTrailerType.T28.name());
  }

  @Test
  @DisplayName("Get ContainerSemiTrailerByLicensePlate Success")
  public void whenGetContainerSemiTrailerByLicensePlate_thenReturnContainerSemiTrailer() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setForwarder(forwarder);
    containerSemiTrailer.setLicensePlate("01234");

    // when
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString()))
        .thenReturn(Optional.of(containerSemiTrailer));

    // then
    ContainerSemiTrailer actualResult = containerSemiTrailerServiceImpl
        .getContainerSemiTrailerByLicensePlate(containerSemiTrailer.getLicensePlate());
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
    assertThat(actualResult.getType()).isEqualTo(EnumTrailerType.T28.name());
  }

  @Test
  @DisplayName("Get ContainerSemiTrailerByLicensePlate NotFound")
  public void whenGetContainerSemiTrailerByLicensePlate_thenReturnNotFoundException_ContainerSemiTrailer() {
    // given
    Forwarder forwarder = new Forwarder();
    forwarder.setId(1L);
    forwarder.setUsername("merchant");

    ContainerSemiTrailer containerSemiTrailer = new ContainerSemiTrailer();
    containerSemiTrailer.setId(1L);
    containerSemiTrailer.setType(EnumTrailerType.T28.name());
    containerSemiTrailer.setUnitOfMeasurement(EnumUnit.KG.name());
    containerSemiTrailer.setForwarder(forwarder);
    containerSemiTrailer.setLicensePlate("01234");

    // when
    when(containerSemiTrailerRepository.findByLicensePlate(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      containerSemiTrailerServiceImpl.getContainerSemiTrailerByLicensePlate(containerSemiTrailer.getLicensePlate());
    });
  }
}
