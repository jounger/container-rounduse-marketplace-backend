package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Geolocation;
import com.crm.payload.request.GeolocationRequest;
import com.crm.repository.DriverRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.GeolocationRepository;
import com.crm.services.GeolocationService;

@Service
public class GeolocationServiceImpl implements GeolocationService {

  @Autowired
  GeolocationRepository geolocationRepository;

  @Autowired
  DriverRepository driverRepository;

  @Autowired
  ForwarderRepository forwarderRepository;

  @Override
  public Geolocation updateGeolocation(Long userId, GeolocationRequest request) {
    if (forwarderRepository.existsById(userId)) {
      Geolocation geolocation = geolocationRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException("Geolocation is not found."));
      if (!geolocation.getDriver().getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned", userId));
      }
      geolocation.setLatitude(request.getLatitude());
      geolocation.setLongitude(request.getLongitude());
      return geolocation;
    } else {
      throw new NotFoundException("Forwarder is not found.");
    }
  }

  @Override
  public Geolocation editGeolocation(Long id, Long userId, Map<String, Object> updates) {
    if (forwarderRepository.existsById(userId)) {
      Geolocation geolocation = geolocationRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("Geolocation is not found."));
      if (!geolocation.getDriver().getForwarder().getId().equals(userId)) {
        throw new InternalException(String.format("Forwarder %s not owned", userId));
      }

      String latitude = String.valueOf(updates.get("latitude"));
      if (latitude != null && !latitude.isEmpty() && !latitude.equals(geolocation.getLatitude())) {
        geolocation.setLatitude(latitude);
      }

      String longitude = String.valueOf(updates.get("longitude"));
      if (longitude != null && !longitude.isEmpty() && !longitude.equals(geolocation.getLongitude())) {
        geolocation.setLongitude(longitude);
      }
      return geolocation;

    } else {
      throw new NotFoundException("Forwarder is not found.");
    }
  }

}
