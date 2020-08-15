package com.crm.models.mapper;

import com.crm.models.FileUpload;
import com.crm.models.dto.FileUploadDto;

public class FileUploadMapper {
  public static FileUploadDto toFileUploadDto(FileUpload fileUpload) {
    if (fileUpload == null) {
      return null;
    }

    FileUploadDto fileUploadDto = new FileUploadDto();
    fileUploadDto.setId(fileUpload.getId());
    fileUploadDto.setOwner(UserMapper.toUserDto(fileUpload.getOwner()));
    fileUploadDto.setName(fileUpload.getName());
    fileUploadDto.setOriginName(fileUpload.getOriginName());
    fileUploadDto.setPath(fileUpload.getPath());
    fileUploadDto.setType(fileUpload.getType());
    return fileUploadDto;
  }
}
