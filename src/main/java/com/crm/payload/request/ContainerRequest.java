package com.crm.payload.request;

import java.util.Set;

import com.crm.models.Address;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerRequest extends SupplyRequest{

	private String driver;
	
	private String forwarder;

    @JsonProperty("container_trailer")
	private String containerTrailer;

    @JsonProperty("container_tractor")
	private String containerTractor;

    @JsonProperty("container_number")
	private String containerNumber;

    @JsonProperty("bl_number")
	private String blNumber;

    @JsonProperty("license_plate")
	private String licensePlate;
	
    @JsonProperty("empty_time")
	private String emptyTime;
	
    @JsonProperty("pick_up_time")
	private String pickUpTime;
	
    @JsonProperty("return_station")
	private Address returnStation;

    @JsonProperty("port_of_delivery")
	private String portOfDelivery;
	
    @JsonProperty("free_time")
	private int freeTime;
    
    private Set<Long> bids;
}
