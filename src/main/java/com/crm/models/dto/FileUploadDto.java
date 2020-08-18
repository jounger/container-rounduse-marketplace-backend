package com.crm.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadDto {

  private Long id;

  private UserDto owner;

  private String name;

  private String originName;

  private String path;

  private String type;
}
