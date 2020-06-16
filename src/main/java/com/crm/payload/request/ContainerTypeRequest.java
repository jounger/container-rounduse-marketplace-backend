package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerTypeRequest {

	@NotBlank
	private String name;
	
	@NotBlank
	private String description;

	@NotBlank
	private String tareWeight;

	@NotBlank
	private String payloadCapacity;

	@NotBlank
	private String cubicCapacity;

	@NotBlank
	@NumberFormat(style = Style.NUMBER)
	private String internalLength;

	@NotBlank 
	@NumberFormat(style = Style.NUMBER)
	private String internalWeight;

	@NotBlank 
	@NumberFormat(style = Style.NUMBER)
	private String internalHeight;

	@NotBlank 
	@NumberFormat(style = Style.NUMBER)
	private String doorOpeningWidth;

	@NotBlank 
	@NumberFormat(style = Style.NUMBER)
	private String doorOpeningHeight;
}
