package com.hulkhiretech.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

  GENERIC_ERROR("2000","Something went wrong .Please try again later"),
  RESOURCE_NOT_FOUND("2001","Invalid Url. please try again"), 
  PAYPAL_PROVIDER_SERVICE_UNAVAILABLE("2002","PayPal-provider Service is currently unavailable.Please try again later"),
  STATUS_PROCESSOR_NOT_FOUND("2003","No Status Processor found for the given status ID"), 
  PAYPAL_PROVIDER_UNKNOWN_ERROR("2004","Unknown error occurred in PayPal-provider"), 
  UNABLE_TO_UPDATE_TRANSACTION("2005","Unable to update transaction details"),;
 
  
  
  private final String errorCode;
  private final String errorMessage;
  
  ErrorCodeEnum(String errorCode,String errorMessage){
    this.errorCode=errorCode;
    this.errorMessage=errorMessage;
  }
}