package com.crm.models.mapper;

import java.util.HashMap;
import java.util.Map;

import com.crm.models.Address;

public class AddressMapper {
  
    public static Map<String, String> toAddressDto(Address address){
      
      Map<String, String> addressdto = new HashMap<>();
      addressdto.put("id", String.valueOf(address.getId()));      
      addressdto.put("street", address.getStreet());      
      addressdto.put("county", address.getCounty());      
      addressdto.put("city", address.getCity()); 
      addressdto.put("country", address.getCountry());
      addressdto.put("postalCode", address.getPostalCode());
      return addressdto;
      
    }
}
