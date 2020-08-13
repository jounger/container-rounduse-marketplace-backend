package com.crm.services;

import com.crm.models.FileUpload;
import com.crm.payload.request.FileUploadRequest;

public interface FileUploadService {

  FileUpload createFileUpload(String username, FileUploadRequest request);

//  FileUpload getFileUpload(Long id);

  FileUpload getFileUpload(String name);
}
