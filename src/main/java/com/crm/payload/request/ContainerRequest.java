package com.crm.payload.request;

import com.crm.models.Address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerRequest {

	private String shippingLineName;

	private String containerType;

	private String status;

	private String driverUsername;

	private String containerTrailer;

	private String containerTractor;

	private String containerNumber;

	private String bLNumber;

	private String licensePlate;
	
	private String emptyTime;
	
	private String pickUpTime;
	
	private Address returnStation;

	private String portName;
	
	private int feeTime;
}
