package com.crm.services;

import java.util.Map;

import com.crm.models.Geolocation;
import com.crm.payload.request.GeolocationRequest;

public interface GeolocationService {

  Geolocation updateGeolocation(String username, GeolocationRequest request);

  Geolocation editGeolocation(Long id, String username, Map<String, Object> updates);
}
