package com.crm.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import com.crm.enums.EnumBidStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.Contract;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Role;
import com.crm.models.ShippingInfo;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.ContractRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.UserRepository;
import com.crm.services.BidService;
import com.crm.services.ContractService;

public class CombinedServiceImplTest {

  private static final Logger logger = LoggerFactory.getLogger(CombinedServiceImplTest.class);

  @InjectMocks
  CombinedServiceImpl combinedServiceImpl;

  @Mock
  private CombinedRepository combinedRepository;

  @Mock
  private BidRepository bidRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BidService bidService;

  @Mock
  private ContainerRepository containerRepository;

  @Mock
  private ContractService contractService;

  PaginationRequest paginationRequest;

  List<Combined> combineds = new ArrayList<>();

  Page<Combined> pages;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setLimit(10);
  }

  @Test
  public void whenCreateCombined_thenReturnCombined() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setOfferee(merchant);
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidClosing(LocalDateTime.now().plusDays(1));
    ;

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setBiddingDocument(biddingDocument);
    bid.setStatus(EnumBidStatus.PENDING.name());

    Container container = new Container();
    container.setId(1L);

    ShippingInfo shippingInfo = new ShippingInfo();
    shippingInfo.setId(1L);

    List<Long> containersId = new ArrayList<>();
    containersId.add(1L);

    ContractRequest contractRequest = new ContractRequest();
    contractRequest.setRequired(true);
    contractRequest.setFinesAgainstContractViolations(2D);
    contractRequest.setContainers(containersId);

    CombinedRequest request = new CombinedRequest();
    request.setContract(contractRequest);

    Combined combined = new Combined();
    combined.setId(1L);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setCombined(combined);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));
    when(combinedRepository.save(Mockito.any(Combined.class))).thenReturn(combined);
    when(contractService.createContract(Mockito.anyLong(), Mockito.anyString(), Mockito.any(ContractRequest.class)))
        .thenReturn(contract);

    // then
    Combined actualResult = combinedServiceImpl.createCombined(bid.getId(), merchant.getUsername(), request);
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(combined.getId());
    logger.info("Response: {}", actualResult.getId());
  }

  @Test
  public void whenCreateCombined_thenReturn400() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Combined combined = new Combined();
    combined.setId(1L);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setCombined(combined);

    CombinedRequest request = new CombinedRequest();

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));

    // then
    Assertions.assertThrows(DuplicateRecordException.class, () -> {
      combinedServiceImpl.createCombined(bid.getId(), merchant.getUsername(), request);
    });
  }

  @Test
  public void whenCreateCombined_thenReturn500() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Outbound outbound = new Outbound();
    outbound.setId(1L);

    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setOutbound(outbound);
    biddingDocument.setOfferee(merchant);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidPrice(1000D);
    bid.setBiddingDocument(biddingDocument);
    bid.setStatus(EnumBidStatus.EXPIRED.name());

    Container container = new Container();
    container.setId(1L);

    List<Long> containersId = new ArrayList<>();
    containersId.add(1L);

    ContractRequest contract = new ContractRequest();
    contract.setRequired(true);
    contract.setFinesAgainstContractViolations(-2D);
    contract.setContainers(containersId);

    CombinedRequest request = new CombinedRequest();
    request.setContract(contract);

    // when
    when(bidRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bid));

    // then
    Assertions.assertThrows(InternalException.class, () -> {
      combinedServiceImpl.createCombined(bid.getId(), merchant.getUsername(), request);
    });
  }

  @Test
  public void whenGetCombinedsByBiddingDocument_thenReturnCombineds() {
    // given
    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Combined combined = new Combined();
    combined.setId(1L);

    combineds.add(combined);
    pages = new PageImpl<Combined>(combineds);

    // when
    when(combinedRepository.findByBiddingDocument(Mockito.anyLong(), Mockito.anyString(),
        Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Combined> actualPages = combinedServiceImpl.getCombinedsByBiddingDocument(biddingDocument.getId(),
        merchant.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getId()).isEqualTo(combined.getId());
  }

  @Test
  public void whenGetCombined_thenReturnCombined() {
    // given
    Combined combined = new Combined();
    combined.setId(1L);

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(combined));

    // then
    Combined actualResult = combinedServiceImpl.getCombined(combined.getId());
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getId()).isEqualTo(1L);
  }

  @Test
  public void whenGetCombined_thenReturn404() {
    // given
    Combined combined = new Combined();
    combined.setId(1L);

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      combinedServiceImpl.getCombined(combined.getId());
    });
  }

  @Test
  public void whenGetCombinedsByUser_thenReturnCombineds() {
    // given
    Role role = new Role();
    role.setId(1L);
    role.setName("ROLE_MERCHANT");

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    merchant.getRoles().add(role);

    Combined combined = new Combined();
    combined.setId(1L);

    combineds.add(combined);
    pages = new PageImpl<Combined>(combineds);

    // when
    when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(merchant));
    when(combinedRepository.findByMerchant(Mockito.anyString(), Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Combined> actualPages = combinedServiceImpl.getCombinedsByUser(merchant.getUsername(), paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getId()).isEqualTo(combined.getId());
  }

  @Test
  public void whenGetCombineds_thenReturnCombineds() {
    // given

    Combined combined = new Combined();
    combined.setId(1L);

    combineds.add(combined);
    pages = new PageImpl<Combined>(combineds);

    // when
    when(combinedRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(pages);

    // then
    Page<Combined> actualPages = combinedServiceImpl.getCombineds(paginationRequest);
    logger.info("actualPages: {}", actualPages);
    assertThat(actualPages.getTotalPages()).isEqualTo(1);
    assertThat(actualPages.getTotalElements()).isEqualTo(1);
    assertThat(actualPages.getContent().size()).isEqualTo(1);
    assertThat(actualPages.getContent().get(0).getId()).isEqualTo(combined.getId());
  }

  @Test
  public void whenEditBid_thenReturnBid() {
    // given
    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    Bid bid = new Bid();
    bid.setId(1L);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);

    boolean isCanceled = true;
    Map<String, Object> updates = new HashMap<>();
    updates.put("isCanceled", isCanceled);

    // when
    when(combinedRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(combined));
    when(bidService.editBid(Mockito.anyLong(), Mockito.anyString(), Mockito.anyMap())).thenReturn(bid);
    when(combinedRepository.save(Mockito.any(Combined.class))).thenReturn(combined);

    // then
    Combined actualResult = combinedServiceImpl.editCombined(combined.getId(), merchant.getUsername(),
        String.valueOf(updates.get("isCanceled")));
    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getIsCanceled()).isEqualTo(isCanceled);
    logger.info("Response: {}", actualResult.getId());
  }

  @Test
  public void whenRemoveCombined_thenReturn404() {
    // given
    Combined combined = new Combined();
    combined.setId(1L);

    // when
    when(combinedRepository.existsById(Mockito.anyLong())).thenReturn(false);

    // then
    Assertions.assertThrows(NotFoundException.class, () -> {
      combinedServiceImpl.removeCombined(combined.getId());
    });
  }
}
