package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
import com.crm.enums.EnumShippingStatus;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BillOfLading;
import com.crm.models.Booking;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.ContainerTractor;
import com.crm.models.ContainerType;
import com.crm.models.Contract;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Inbound;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.QRToken;
import com.crm.models.ShippingInfo;
import com.crm.models.ShippingLine;
import com.crm.payload.request.QRTokenRequest;
import com.crm.services.QRTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class QRTokenControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(UserControllerIT.class);

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private QRTokenService qrTokenService;

  @Autowired
  private ObjectMapper objectMapper;

  private QRTokenRequest qrTokenRequest;

  QRToken qrToken;

  LocalDateTime timeNow = LocalDateTime.now();

  @BeforeEach
  void setup() {
    qrTokenRequest = new QRTokenRequest();
    qrTokenRequest.setToken("123s4142tweqw4wrc2343rwd");
    qrToken = new QRToken();
    qrToken.setId(1L);
    qrToken.setToken("123s4142tweqw4wrc2343rwd");
    qrToken.setExpiredDate(timeNow.plusHours(3));
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void createQRToken_thenStatusOkAndReturnToken() throws Exception {
    // given
    when(qrTokenService.createQRToken(Mockito.anyString(), Mockito.anyLong())).thenReturn(qrToken);

    // when and then
    MvcResult mvcResult = mockMvc.perform(post("/api/qr-token/shipping-info/1").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isCreated()).andReturn();

    // RESPONSE
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "driver", roles = { "Driver" })
  void isValidQRTolken_thenStatusOkAndReturnTrue() throws Exception {
    // given
    when(qrTokenService.isValidQRTolken(Mockito.anyString())).thenReturn(true);

    // when and then
    MvcResult mvcResult = mockMvc
        .perform(get("/api/qr-token").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(qrTokenRequest)).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data").value(true)).andReturn();

    // RESPONSE
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "driver", roles = { "DRIVER" })
  void resetPasswordByToken_thenStatusOkAndReturnSuccessMessage() throws Exception {
    // given
    BiddingDocument biddingDocument = new BiddingDocument();
    biddingDocument.setId(1L);
    biddingDocument.setBidOpening(timeNow);
    biddingDocument.setBidClosing(timeNow.plusHours(8));
    biddingDocument.setIsMultipleAward(false);
    biddingDocument.setBidFloorPrice(2000D);
    biddingDocument.setPriceLeadership(2000D);
    biddingDocument.setBidPackagePrice(3000D);
    biddingDocument.setCurrencyOfPayment("VND");
    biddingDocument.setStatus(EnumBiddingStatus.BIDDING.name());

    Merchant merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");
    biddingDocument.setOfferee(merchant);

    Forwarder forwarder = new Forwarder();
    forwarder.setId(2L);
    forwarder.setUsername("forwarder");

    Driver driver = new Driver();
    driver.setId(3L);
    driver.setUsername("driver");
    driver.setForwarder(forwarder);

    ShippingLine shippingLine = new ShippingLine();
    shippingLine.setCompanyCode("SPL1");

    Port port = new Port();
    port.setNameCode("PortTest");

    ContainerType containerType = new ContainerType();
    containerType.setName("CT12");

    Booking booking = new Booking();
    booking.setNumber("BK123w22");
    booking.setUnit(3);
    booking.setCutOffTime(timeNow.plusDays(30));
    booking.setIsFcl(false);
    booking.setPortOfLoading(port);

    Outbound outbound = new Outbound();
    outbound.setId(1L);
    outbound.setBooking(booking);
    outbound.setContainerType(containerType);
    outbound.setDeliveryTime(timeNow.plusDays(10));
    outbound.setGoodsDescription("Abc");
    outbound.setGrossWeight(1233D);
    outbound.setPackingStation("Ha Noi");
    outbound.setPackingTime(timeNow.plusDays(9));
    outbound.setUnitOfMeasurement("3");
    outbound.setShippingLine(shippingLine);

    biddingDocument.setOutbound(outbound);

    ContainerTractor tractor = new ContainerTractor();
    tractor.setId(1L);

    ContainerSemiTrailer trailer = new ContainerSemiTrailer();
    trailer.setId(2L);

    List<Container> containers = new ArrayList<>();
    Container container = new Container();
    container.setId(1L);
    container.setDriver(driver);
    container.setNumber("CN2d2d22");
    container.setTractor(tractor);
    container.setTrailer(trailer);

    containers.add(container);

    Inbound inbound = new Inbound();
    inbound.setId(1L);
    inbound.setForwarder(forwarder);
    inbound.setEmptyTime(timeNow.plusDays(10));
    inbound.setPickupTime(timeNow.plusDays(9));

    BillOfLading billOfLading = new BillOfLading();
    billOfLading.setFreeTime(timeNow.plusDays(12));
    billOfLading.setId(1L);
    billOfLading.setPortOfDelivery(port);
    billOfLading.getContainers().add(container);
    billOfLading.setInbound(inbound);

    Bid bid = new Bid();
    bid.setId(1L);
    bid.setBidder(forwarder);
    bid.setBiddingDocument(biddingDocument);
    bid.setBidDate(timeNow);
    bid.setBidPrice(2300D);
    bid.setFreezeTime(timeNow.plusHours(1));
    bid.setValidityPeriod(timeNow.minusHours(1));
    bid.setStatus(EnumBidStatus.PENDING.name());
    bid.setContainers(containers);

    List<Bid>listBids = new ArrayList<Bid>();
    listBids.add(bid);

    Contract contract = new Contract();
    contract.setId(1L);
    contract.setFinesAgainstContractViolations(8D);
    contract.setRequired(false);
    contract.setCreationDate(timeNow);

    Combined combined = new Combined();
    combined.setId(1L);
    combined.setBid(bid);
    combined.setIsCanceled(false);
    combined.setContract(contract);

    ShippingInfo shippingInfo = new ShippingInfo();
    shippingInfo.setId(1L);
    shippingInfo.setContract(contract);
    shippingInfo.setContainer(container);
    shippingInfo.setOutbound(outbound);
    shippingInfo.setStatus(EnumShippingStatus.INFO_RECEIVED.name());
    
    when(qrTokenService.editShippingInfoByToken(Mockito.anyString(), Mockito.anyString())).thenReturn(shippingInfo);

    // when and then
    MvcResult mvcResult = mockMvc
        .perform(patch("/api/qr-token").contentType(MediaType.APPLICATION_JSON)
            .header("Authentication", "123sad12easdq2eqd212r").accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Cập nhật trạng thái đơn vận chuyển thành công")).andReturn();

    // RESPONSE
    MockHttpServletResponse response = mvcResult.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
