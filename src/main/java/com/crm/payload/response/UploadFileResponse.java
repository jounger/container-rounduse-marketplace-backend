package com.crm.payload.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private String fileName;

  private String fileDownloadUri;

  private String fileType;

  private long size;
}
