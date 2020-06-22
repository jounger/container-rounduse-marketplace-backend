package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerTypeRequest {
  
    private Long id;

	@NotBlank
	private String name;
	
	@NotBlank
	private String description;

	@NotBlank
	@JsonProperty("tare_weight")
	private String tareWeight;

	@NotBlank
	@JsonProperty("payload_capacity")
	private String payloadCapacity;

	@NotBlank
	@JsonProperty("cubic_capacity")
	private String cubicCapacity;

	@NotBlank
	@NumberFormat(style = Style.NUMBER)
	@JsonProperty("internal_length")
	private String internalLength;

	@NotBlank 
	@NumberFormat(style = Style.NUMBER)
	@JsonProperty("internal-width")
	private String internalWidth;

	@NotBlank 
	@NumberFormat(style = Style.NUMBER)
	@JsonProperty("internal_height")
	private String internalHeight;

	@NotBlank 
	@NumberFormat(style = Style.NUMBER)
	@JsonProperty("door_opening_width")
	private String doorOpeningWidth;

	@NotBlank 
	@NumberFormat(style = Style.NUMBER)
	@JsonProperty("door_opening_height")
	private String doorOpeningHeight;
}
