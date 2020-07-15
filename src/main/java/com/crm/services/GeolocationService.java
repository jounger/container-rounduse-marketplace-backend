package com.crm.services;

import java.util.Map;

import com.crm.models.Geolocation;
import com.crm.payload.request.GeolocationRequest;

public interface GeolocationService {

  Geolocation updateGeolocation(Long userId, GeolocationRequest request);

  Geolocation editGeolocation(Long id, Long userId, Map<String, Object> updates);
}
