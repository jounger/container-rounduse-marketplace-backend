package com.crm.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import com.crm.models.Booking;
import com.crm.models.ContainerType;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.Port;
import com.crm.models.ShippingLine;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
class BookingControllerIT {

  private static final Logger logger = LoggerFactory.getLogger(BookingControllerIT.class);

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BookingService bookingService;

  @Autowired
  private ObjectMapper objectMapper;

  PaginationRequest paginationRequest;

  Page<Booking> pages;

  LinkedMultiValueMap<String, String> requestParams;

  LocalDateTime timeNow = LocalDateTime.now();

  Merchant merchant;

  Booking booking;

  Port port;

  Outbound outbound;

  ContainerType containerType;

  ShippingLine shippingLine;

  @BeforeEach
  public void setUp() {
    merchant = new Merchant();
    merchant.setId(1L);
    merchant.setUsername("merchant");

    shippingLine = new ShippingLine();
    shippingLine.setCompanyCode("SPL1");

    port = new Port();
    port.setNameCode("PortTest");

    containerType = new ContainerType();
    containerType.setName("CT12");

    booking = new Booking();
    booking.setId(1L);
    booking.setNumber("BK123w22");
    booking.setUnit(3);
    booking.setCutOffTime(timeNow.plusDays(30));
    booking.setIsFcl(false);
    booking.setPortOfLoading(port);

    outbound = new Outbound();
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

    requestParams = new LinkedMultiValueMap<>();
    requestParams.add("page", "0");
    requestParams.add("limit", "10");
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void searchBookings_thenStatusOk_andReturnBookings() throws Exception {
    // given
    String search = "number:BK123w22";
    requestParams.add("search", search);
    List<Booking> bookings = new ArrayList<Booking>();
    bookings.add(booking);
    pages = new PageImpl<Booking>(bookings);
    when(bookingService.searchBookings(Mockito.any(PaginationRequest.class), Mockito.anyString()))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/booking/filter").contentType(MediaType.APPLICATION_JSON).params(requestParams)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].number").value("BK123w22")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBookingsByInbound_thenStatusOk_andReturnBookings() throws Exception {
    // given
    List<Booking> bookings = new ArrayList<Booking>();
    bookings.add(booking);
    pages = new PageImpl<Booking>(bookings);
    when(bookingService.getBookingsByOutbound(Mockito.anyLong(), Mockito.any(PaginationRequest.class)))
        .thenReturn(pages);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/booking/outbound/1").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1))
        .andExpect(jsonPath("$.data[0].number").value("BK123w22")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBookingByNumber_thenStatusOk_andReturnBookings() throws Exception {
    // given
    requestParams = new LinkedMultiValueMap<String, String>();
    requestParams.add("number", "2esa2ss");
    when(bookingService.getBookingsByNumber(Mockito.anyString()))
        .thenReturn(booking);

    // when and then
    MvcResult result = mockMvc
        .perform(get("/api/booking").contentType(MediaType.APPLICATION_JSON_VALUE).params(requestParams))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.number").value("BK123w22")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void getBooking_thenStatusOk_andReturnBooking() throws Exception {
    // given
    when(bookingService.getBookingById(Mockito.anyLong())).thenReturn(booking);
    // when and then
    MvcResult result = mockMvc.perform(get("/api/booking/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.number").value("BK123w22")).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

  @Test
  @WithMockUser(username = "merchant", roles = { "MERCHANT" })
  void editBooking_thenStatusOk_andReturnBooking() throws Exception {
    // given
    Map<String, String> updates = new HashMap<String, String>();
    updates.put("unit", "5");
    booking.setUnit(5);
    when(bookingService.editBooking(Mockito.anyMap(), Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(booking);

    // when and then
    MvcResult result = mockMvc
        .perform(patch("/api/booking/1").contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updates)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.unit").value(5)).andReturn();

    // print response
    MockHttpServletResponse response = result.getResponse();
    logger.info("Reponse: {}", response.getContentAsString());
  }

}
