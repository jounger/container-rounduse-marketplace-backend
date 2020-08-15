package com.crm.models.mapper;

import com.crm.models.Geolocation;
import com.crm.models.dto.GeolocationDto;

public class GeolocationMapper {
  public static GeolocationDto toGeolocationDto(Geolocation geolocation) {
    if (geolocation == null) {
      return null;
    }

    GeolocationDto geolocationDto = new GeolocationDto();
    geolocationDto.setId(geolocation.getId());
    geolocationDto.setLatitude(geolocation.getLatitude());
    geolocationDto.setLongitude(geolocation.getLongitude());
    return geolocationDto;
  }
}
