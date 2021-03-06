package com.crm.controllers;

import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.Geolocation;
import com.crm.models.dto.GeolocationDto;
import com.crm.models.mapper.GeolocationMapper;
import com.crm.payload.response.DefaultResponse;
import com.crm.services.GeolocationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/geolocation")
public class GeolocationController {

  private static final Logger logger = LoggerFactory.getLogger(GeolocationController.class);

  @Autowired
  GeolocationService geolocationService;

  @Transactional
  @PreAuthorize("hasRole('FORWARDER') or hasRole('DRIVER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editGeolocation(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Geolocation geolocation = geolocationService.editGeolocation(id, username, updates);
    GeolocationDto geolocationDto = GeolocationMapper.toGeolocationDto(geolocation);

    // Set default response body
    DefaultResponse<GeolocationDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_GEOLOCATION_SUCCESSFULLY);
    defaultResponse.setData(geolocationDto);

    logger.info("User {} editGeolocation from id {} with request: {}", username, id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

}
