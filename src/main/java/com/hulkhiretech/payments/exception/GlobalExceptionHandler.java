package com.hulkhiretech.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.pojo.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ProcessingServiceException.class)
  public ResponseEntity<ErrorResponse> handlePaypalException(ProcessingServiceException ex){
    
    ErrorResponse error=new ErrorResponse(ex.getErrorCode(),ex.getErrorMessage());
    
    return new ResponseEntity<>(error,ex.getHttpStatus());
    
    
  }
  
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex){
	
	ErrorResponse error=new ErrorResponse(ErrorCodeEnum.RESOURCE_NOT_FOUND.getErrorCode(),ErrorCodeEnum.RESOURCE_NOT_FOUND.getErrorMessage());
	
	return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
	
	
  }
  
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex){
    
    ErrorResponse error=new ErrorResponse(ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),ErrorCodeEnum.GENERIC_ERROR.getErrorMessage());
    
    return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    
    
  }
}