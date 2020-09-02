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

import com.crm.exception.ForbiddenException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.Contract;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.payload.request.ContractRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContractRepository;
import com.crm.repository.SupplierRepository;
import com.crm.services.BidService;
import com.crm.services.ShippingInfoService;

public class ContractServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(ContractServiceImplTest.class);

  @InjectMocks
  ContractServiceImp contractServiceImp;

  @Mock
  CombinedRepository combinedRepository;

  @Mock
  private ContractRepository contractRepository;

  @Mock
  private BidService bidService;

  @Mock
  private ShippingInfoService shippingInfoService;

  @Mock
  private SupplierRepository supplierRepository;

  PaginationRequest paginationRequest;

  Page<Contract> pages;

  List<Contract> contracts;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    contracts = new ArrayList<>();

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
  @DisplayName("Create Contract success")
  public void whenCreateDriver_thenReturnContract() {

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    ContractRequest contractRequest = new ContractRequest();
    contractRequest.setContainers(containerIds);
    contractRequest.setPrice(100D);
    contractRequest.setRequired(true);
    contractRequest.setFinesAgainstContractViolations(1D);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(combined));
    when(supplierRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));
    when(contractRepository.save(Mockito.any(Contract.class))).thenReturn(contract);
    when(bidService.editBidWhenCombined(Mockito.anyLong(), Mockito.anyString(), Mockito.anyList())).thenReturn(bid);

    // then
    Contract actualResult = contractServiceImp.createContract(combined.getId(), offeree.getUsername(), contractRequest);
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
  }

  @Test
  @DisplayName("Create Contract when combined notfound")
  public void whenCreateDriver_thenReturn404_Combined() {

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    ContractRequest contractRequest = new ContractRequest();
    contractRequest.setContainers(containerIds);
    contractRequest.setPrice(100D);
    contractRequest.setRequired(true);
    contractRequest.setFinesAgainstContractViolations(1D);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      contractServiceImp.createContract(combined.getId(), offeree.getUsername(), contractRequest);
    });
  }

  @Test
  @DisplayName("Create Contract when AccessDenied")
  public void whenCreateDriver_thenReturnAccessDeniedException() {

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    ContractRequest contractRequest = new ContractRequest();
    contractRequest.setContainers(containerIds);
    contractRequest.setPrice(100D);
    contractRequest.setRequired(true);
    contractRequest.setFinesAgainstContractViolations(1D);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(combined));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      contractServiceImp.createContract(combined.getId(), "XXXX", contractRequest);
    });
  }

  @Test
  @DisplayName("Create Contract when User notfound")
  public void whenCreateDriver_thenReturn404_User() {

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    ContractRequest contractRequest = new ContractRequest();
    contractRequest.setContainers(containerIds);
    contractRequest.setPrice(100D);
    contractRequest.setRequired(true);
    contractRequest.setFinesAgainstContractViolations(1D);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(combined));
    when(supplierRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      contractServiceImp.createContract(combined.getId(), offeree.getUsername(), contractRequest);
    });
  }

  @Test
  @DisplayName("Create Contract when Container notfound")
  public void whenCreateDriver_thenReturn404_Container() {

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    ContractRequest contractRequest = new ContractRequest();
    contractRequest.setContainers(containerIds);
    contractRequest.setPrice(100D);
    contractRequest.setRequired(true);
    contractRequest.setFinesAgainstContractViolations(1D);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(false);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(combined));
    when(supplierRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(offeree));

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      contractServiceImp.createContract(combined.getId(), offeree.getUsername(), contractRequest);
    });
  }

  @Test
  @DisplayName("Get Contract By Combined success")
  public void whenGetContractByCombined_thenReturnContract() {

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    ContractRequest contractRequest = new ContractRequest();
    contractRequest.setContainers(containerIds);
    contractRequest.setPrice(100D);
    contractRequest.setRequired(true);
    contractRequest.setFinesAgainstContractViolations(1D);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(combined));
    when(contractRepository.findByCombined(Mockito.anyLong(), Mockito.anyString())).thenReturn(Optional.of(contract));

    // then
    Contract actualResult = contractServiceImp.getContractByCombined(combined.getId(), offeree.getUsername());
    logger.info("actualResult: {}", actualResult);
    assertThat(actualResult).isNotNull();
  }

  @Test
  @DisplayName("Get Contract By Combined when combined notFound")
  public void whenGetContractByCombined_thenReturn404_Combined() {

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    ContractRequest contractRequest = new ContractRequest();
    contractRequest.setContainers(containerIds);
    contractRequest.setPrice(100D);
    contractRequest.setRequired(true);
    contractRequest.setFinesAgainstContractViolations(1D);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      contractServiceImp.getContractByCombined(combined.getId(), offeree.getUsername());
    });
  }

  @Test
  @DisplayName("Get Contract By Combined when Contract notFound")
  public void whenGetContractByCombined_thenReturn404_Contract() {
    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(combined));
    when(contractRepository.findByCombined(Mockito.anyLong(), Mockito.anyString())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      contractServiceImp.getContractByCombined(combined.getId(), offeree.getUsername());
    });
  }

  @Test
  @DisplayName("Get Contract By User success")
  public void whenGetContractByUser_thenReturnContract() {

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setPrice(100D);
    contract.setRequired(true);
    contract.setFinesAgainstContractViolations(1D);

    contracts.add(contract);
    pages = new PageImpl<>(contracts);

    // when
    when(contractRepository.findByUser(Mockito.anyString(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Contract> actualPages = contractServiceImp.getContractsByUser(offeree.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
  }

  @Test
  @DisplayName("Remove Contract Success")
  public void whenRemoveContract_thenReturnContract() {
    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);
    bid.setBidder(bidder);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setPrice(100D);
    contract.setRequired(true);
    contract.setFinesAgainstContractViolations(1D);
    contract.setCombined(combined);

    // when
    when(contractRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(contract));

    // then
    contractServiceImp.removeContract(combined.getId(), offeree.getUsername());
  }

  @Test
  @DisplayName("Remove Contract when AccessDenied")
  public void whenRemoveContract_thenReturnAccessDeniedException() {
    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);
    bid.setBidder(bidder);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setPrice(100D);
    contract.setRequired(true);
    contract.setFinesAgainstContractViolations(1D);
    contract.setCombined(combined);

    // when
    when(contractRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(contract));

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      contractServiceImp.removeContract(combined.getId(), "XXXXX");
    });
  }

  @Test
  @DisplayName("Remove Contract when contract notfound")
  public void whenRemoveContract_thenReturn404_Contract() {
    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    Forwarder bidder = new Forwarder();
    bidder.setId(1L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);
    bid.setBidder(bidder);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setPrice(100D);
    contract.setRequired(true);
    contract.setFinesAgainstContractViolations(1D);
    contract.setCombined(combined);

    // when
    when(contractRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      contractServiceImp.removeContract(combined.getId(), offeree.getUsername());
    });
  }

  @Test
  @DisplayName("Edit Contract success")
  public void whenSearchContract_thenReturnContract() {

    Map<String, Object> updates = new HashMap<>();
    updates.put("required", true);
    updates.put("price", 1000D);

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setPrice(200D);
    contract.setRequired(false);
    contract.setFinesAgainstContractViolations(1D);
    contract.setCombined(combined);

    // when
    when(contractRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(contract));
    when(contractRepository.isUnpaidContract(Mockito.anyLong())).thenReturn(true);
    when(contractRepository.save(Mockito.any(Contract.class))).thenReturn(contract);

    // then
    Contract actualResult = contractServiceImp.editContract(contract.getId(), offeree.getUsername(), updates);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(contract.getId());
    assertThat(actualResult.getCombined().getId()).isEqualTo(combined.getId());
  }

  @Test
  @DisplayName("Edit Contract when contract notFound")
  public void whenSearchContract_thenReturn404_Contract() {

    Map<String, Object> updates = new HashMap<>();
    updates.put("required", true);
    updates.put("price", 1000);

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setPrice(100D);
    contract.setRequired(true);
    contract.setFinesAgainstContractViolations(1D);
    contract.setCombined(combined);

    // when
    when(contractRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    when(contractRepository.isUnpaidContract(Mockito.anyLong())).thenReturn(true);
    when(contractRepository.save(Mockito.any(Contract.class))).thenReturn(contract);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      contractServiceImp.editContract(contract.getId(), offeree.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit Contract when contract invalid edit")
  public void whenSearchContract_thenReturnContractInvalidEditException() {

    Map<String, Object> updates = new HashMap<>();
    updates.put("required", false);
    updates.put("price", 1000);

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setPrice(100D);
    contract.setRequired(false);
    contract.setFinesAgainstContractViolations(1D);
    contract.setCombined(combined);

    // when
    when(contractRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(contract));
    when(contractRepository.isUnpaidContract(Mockito.anyLong())).thenReturn(false);
    when(contractRepository.save(Mockito.any(Contract.class))).thenReturn(contract);

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      contractServiceImp.editContract(contract.getId(), offeree.getUsername(), updates);
    });
  }

  @Test
  @DisplayName("Edit Contract when AccessDenied")
  public void whenSearchContract_thenReturnAccessDeniedException() {

    Map<String, Object> updates = new HashMap<>();
    updates.put("required", false);
    updates.put("price", 1000);

    List<Long> containerIds = new ArrayList<Long>();
    containerIds.add(1L);
    containerIds.add(2L);

    Merchant offeree = new Merchant();
    offeree.setId(1L);
    offeree.setUsername("merchant");

    Forwarder bidder = new Forwarder();
    bidder.setId(2L);
    bidder.setUsername("forwarder");

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOfferee(offeree);
    biddingDocument.setIsMultipleAward(true);

    Container container = new Container();
    container.setId(1L);

    Container container1 = new Container();
    container1.setId(2L);

    Collection<Container> containers = new ArrayList<>();
    containers.add(container);
    containers.add(container1);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBiddingDocument(biddingDocument);
    bid.setContainers(containers);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setPrice(100D);
    contract.setRequired(false);
    contract.setFinesAgainstContractViolations(1D);
    contract.setCombined(combined);

    // when
    when(contractRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(contract));
    when(contractRepository.isUnpaidContract(Mockito.anyLong())).thenReturn(true);
    when(contractRepository.save(Mockito.any(Contract.class))).thenReturn(contract);

    // then
    Assertions.assertThrows(ForbiddenException.class, () -> {
      contractServiceImp.editContract(contract.getId(), bidder.getUsername(), updates);
    });
  }
}
