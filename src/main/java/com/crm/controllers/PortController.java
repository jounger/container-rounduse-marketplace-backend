package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.Port;
import com.crm.models.dto.PortDto;
import com.crm.models.mapper.PortMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.PortRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.PortService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/port")
public class PortController {

  private static final Logger logger = LoggerFactory.getLogger(PortController.class);

  @Autowired
  private PortService portService;

  @GetMapping("")
  public ResponseEntity<?> getPorts(@Valid PaginationRequest request) {

    Page<Port> pages = portService.getPorts(request);
    PaginationResponse<PortDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Port> ports = pages.getContent();
    List<PortDto> portsDto = new ArrayList<>();
    ports.forEach(port -> portsDto.add(PortMapper.toPortDto(port)));
    response.setContents(portsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getPort(@PathVariable Long id) {
    Port port = portService.getPortById(id);
    PortDto portDto = new PortDto();
    portDto = PortMapper.toPortDto(port);
    return ResponseEntity.ok(portDto);
  }

  @RequestMapping(method = RequestMethod.GET, params = { "nameCode" })
  public ResponseEntity<?> getPortByNameCode(@RequestParam String nameCode) {
    Port port = portService.getPortByNameCode(nameCode);
    PortDto portDto = new PortDto();
    portDto = PortMapper.toPortDto(port);
    return ResponseEntity.ok(portDto);
  }

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> createPort(@Valid @RequestBody PortRequest request) {
    Port port = portService.createPort(request);
    PortDto portDto = PortMapper.toPortDto(port);

    // Set default response body
    DefaultResponse<PortDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_PORT_SUCCESSFULLY);
    defaultResponse.setData(portDto);

    logger.info("Create Port with request: {}", request.toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('MODERATOR')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editPort(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {
    Port port = portService.editPort(updates, id);
    PortDto portDto = PortMapper.toPortDto(port);

    // Set default response body
    DefaultResponse<PortDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_PORT_SUCCESSFULLY);
    defaultResponse.setData(portDto);

    logger.info("editPort from id {} with request: {}", id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> removePort(@PathVariable Long id) {
    portService.removePort(id);

    // Set default response body
    DefaultResponse<PortDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_PORT_SUCCESSFULLY);

    logger.info("DeletePort from port id {}", id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}
