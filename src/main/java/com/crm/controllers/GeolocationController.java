package com.crm.controllers;

import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Geolocation;
import com.crm.models.dto.GeolocationDto;
import com.crm.models.mapper.GeolocationMapper;
import com.crm.payload.request.GeolocationRequest;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.GeolocationService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/geolocation")
public class GeolocationController {

  @Autowired
  GeolocationService geolocationService;

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('DRIVER')")
  public ResponseEntity<?> updateGeolocation(@Valid @RequestBody GeolocationRequest request) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Geolocation geolocation = geolocationService.updateGeolocation(userId, request);
    GeolocationDto geolocationDto = GeolocationMapper.toGeolocationDto(geolocation);
    return ResponseEntity.ok(geolocationDto);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER') or hasRole('DRIVER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editGeolocation(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Geolocation geolocation = geolocationService.editGeolocation(id, userId, updates);
    GeolocationDto geolocationDto = GeolocationMapper.toGeolocationDto(geolocation);
    return ResponseEntity.ok(geolocationDto);
  }

}
