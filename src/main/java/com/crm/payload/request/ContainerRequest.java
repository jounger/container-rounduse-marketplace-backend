package com.crm.payload.request;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerRequest extends SupplyRequest{

	private String driver;
	
	private String forwarder;

	private String containerTrailer;

	private String containerTractor;

	private String containerNumber;

	private String blNumber;

	private String licensePlate;
	
	private String emptyTime;
	
	private String pickUpTime;
	
	private String returnStation;

	private String portOfDelivery;
	
	private int freeTime;
    
    private Set<Long> bids;
}
