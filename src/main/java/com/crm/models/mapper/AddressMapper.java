package com.crm.models.mapper;

import java.util.HashMap;
import java.util.Map;

import com.crm.models.Address;

public class AddressMapper {
  
    public static Map<String, String> toAddressHashMap(Address address){
      
      Map<String, String> addressHashMap = new HashMap<>();
      addressHashMap.put("id", String.valueOf(address.getId()));      
      addressHashMap.put("street", address.getStreet());      
      addressHashMap.put("county", address.getCounty());      
      addressHashMap.put("city", address.getCity()); 
      addressHashMap.put("country", address.getCountry());
      addressHashMap.put("postalCode", address.getPostalCode());
      return addressHashMap;
      
    }
}
