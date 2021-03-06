package com.crm.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ExceptionController {

  private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handlerNotFoundException(NotFoundException ex, WebRequest req) {

    logger.error("Runtime error: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse();
    error.setStatus(HttpStatus.NOT_FOUND);
    error.setMessage(ex.getMessage());
    return error;
  }

  @ExceptionHandler(DuplicateRecordException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handlerDuplicateRecordException(DuplicateRecordException ex, WebRequest req) {

    logger.error("Runtime error: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse();
    error.setStatus(HttpStatus.BAD_REQUEST);
    error.setMessage(ex.getMessage());
    return error;
  }

  @ExceptionHandler(InternalException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handlerException(InternalException ex, WebRequest req) {

    logger.error("Internal Server Error: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse();
    error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    error.setMessage(ex.getMessage());
    return error;
  }

  @ExceptionHandler(ForbiddenException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorResponse handlerException(ForbiddenException ex, WebRequest req) {

    logger.error("Forbidden Error: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse();
    error.setStatus(HttpStatus.FORBIDDEN);
    error.setMessage(ex.getMessage());
    return error;
  }

  @ExceptionHandler(FileStorageException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handlerException(FileStorageException ex, WebRequest req) {

    logger.error("File Storage Error: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse();
    error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    error.setMessage(ex.getMessage());
    return error;
  }

  @ExceptionHandler(MyFileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handlerException(MyFileNotFoundException ex, WebRequest req) {

    logger.error("MyFile Not Found Error: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse();
    error.setStatus(HttpStatus.NOT_FOUND);
    error.setMessage(ex.getMessage());
    return error;
  }

  @ExceptionHandler(UnauthorizedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorResponse handlerException(UnauthorizedException ex, WebRequest req) {

    logger.error("Unauthorized Error: {}", ex.getMessage());

    ErrorResponse error = new ErrorResponse();
    error.setStatus(HttpStatus.UNAUTHORIZED);
    error.setMessage(ex.getMessage());
    return error;
  }

}
