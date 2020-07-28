package com.crm.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorConstant;
import com.crm.common.Tool;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Geolocation;
import com.crm.payload.request.GeolocationRequest;
import com.crm.repository.DriverRepository;
import com.crm.repository.GeolocationRepository;
import com.crm.services.GeolocationService;

@Service
public class GeolocationServiceImpl implements GeolocationService {

  @Autowired
  GeolocationRepository geolocationRepository;

  @Autowired
  DriverRepository driverRepository;

  @Override
  public Geolocation updateGeolocation(String username, GeolocationRequest request) {
    if (driverRepository.existsByUsername(username)) {
      Geolocation geolocation = geolocationRepository.findById(request.getId())
          .orElseThrow(() -> new NotFoundException(ErrorConstant.GEOLOCATION_NOT_FOUND));
      if (!geolocation.getDriver().getUsername().equals(username)) {
        throw new InternalException(ErrorConstant.USER_ACCESS_DENIED);
      }
      geolocation.setLatitude(request.getLatitude());
      geolocation.setLongitude(request.getLongitude());

      geolocationRepository.save(geolocation);
      return geolocation;
    } else {
      throw new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND);
    }
  }

  @Override
  public Geolocation editGeolocation(Long id, String username, Map<String, Object> updates) {
    if (driverRepository.existsByUsername(username)) {
      Geolocation geolocation = geolocationRepository.findById(id)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.GEOLOCATION_NOT_FOUND));
      if (!geolocation.getDriver().getUsername().equals(username)) {
        throw new InternalException(ErrorConstant.USER_ACCESS_DENIED);
      }

      String latitude = String.valueOf(updates.get("latitude"));
      if (updates.get("latitude") != null && !Tool.isEqual(geolocation.getLatitude(), latitude)) {
        geolocation.setLatitude(latitude);
      }

      String longitude = String.valueOf(updates.get("longitude"));
      if (updates.get("longitude") != null && !Tool.isEqual(geolocation.getLongitude(), longitude)) {
        geolocation.setLongitude(longitude);
      }

      geolocationRepository.save(geolocation);
      return geolocation;

    } else {
      throw new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND);
    }
  }

}
