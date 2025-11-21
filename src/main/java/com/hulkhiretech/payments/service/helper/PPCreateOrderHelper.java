package com.hulkhiretech.payments.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.dto.TransactionDto;

import com.hulkhiretech.payments.exception.ProcessingServiceException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypalprovider.PPCreateOrderReq;
import com.hulkhiretech.payments.paypalprovider.PPErrorResponse;
import com.hulkhiretech.payments.paypalprovider.PPOrderResponse;
import com.hulkhiretech.payments.pojo.InitiatePaymentRequest;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PPCreateOrderHelper {

	 private final JsonUtil jsonUtil;
	 
	 @Value("${paypal.provider.create.order.url}")
	 private String paypalProviderCreateOrderUrl ;
	 
	public HttpRequest prepareHttpRequest(String txnReference, InitiatePaymentRequest initiatePaymentRequest, TransactionDto txnDto) {
	
		log.info("Preparing HttpRequest for PayPal create order for txnReference: {} ", txnReference +","
				+ " initiatePaymentRequest: {} , txnDto: {}", initiatePaymentRequest, txnDto);
		
		//create header
		HttpHeaders headers=new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	   PPCreateOrderReq ppCreateOrderReq=new PPCreateOrderReq();
	   ppCreateOrderReq.setAmount(txnDto.getAmount().doubleValue());
	   ppCreateOrderReq.setCurrencyCode(txnDto.getCurrency());
	   ppCreateOrderReq.setReturnUrl(initiatePaymentRequest.getSuccessUrl());
	   ppCreateOrderReq.setCancelUrl(initiatePaymentRequest.getCancelUrl());
	   
	   //covert to json
	   String requestAsJson=jsonUtil.toJson(ppCreateOrderReq);
	   
	     
		//prepare httpRequest
	    HttpRequest httpRequest=new HttpRequest();
	    httpRequest.setHttpMethod(HttpMethod.POST);
	   
		httpRequest.setUrl(paypalProviderCreateOrderUrl);//paypal create order endpoint
	    httpRequest.setBody(requestAsJson);
	    httpRequest.setHttpHeaders(headers);
	    
	    log.info("Prepared HttpRequest for PayPal create order: {}", httpRequest);
	    return httpRequest;
		
	}

	public PPOrderResponse handlePaypalResponse(ResponseEntity<String> httpResponse) {
		
		log.info("Handling PayPal create order response: {}", httpResponse);
		
		if(httpResponse.getStatusCode().equals(HttpStatus.OK)) {
			String responseBody=httpResponse.getBody();
			log.info("PayPal create order successful response body: {}", responseBody);
			
			PPOrderResponse ppOrderResponse=jsonUtil.fromJson(responseBody, PPOrderResponse.class);
			
			log.info("Mapped PayPal create order response to PPOrderResponse: {}", ppOrderResponse);
			
			if(ppOrderResponse!=null 
					&& ppOrderResponse.getPaypalStatus()!=null
					&& ppOrderResponse.getOrderId()!=null
					&& ppOrderResponse.getRedirectUrl()!=null) {
				
			   log.info("PayPal Order created successfully with Order ID: {} and Redirect URL: {}",
					   ppOrderResponse.getOrderId(), ppOrderResponse.getRedirectUrl());
			   
			   return ppOrderResponse;
				
			}
			else {
				log.error("Invalid PayPal create order response: {}", ppOrderResponse);
				
			}
			
		
	}
		//TODO: handle 4xx,5xx errors from paypal and throw custom exceptions
		if(httpResponse.getStatusCode().is4xxClientError()
				|| httpResponse.getStatusCode().is5xxServerError()) {
			log.error("Error response from PayPal create order: Status Code: {}, Body: {}",
					httpResponse.getStatusCode(), httpResponse.getBody());
			
			PPErrorResponse errorResponse=jsonUtil.fromJson(httpResponse.getBody(),PPErrorResponse.class);
			
			throw new ProcessingServiceException(errorResponse.getErrorCode(), errorResponse.getErrorMessage()
					,HttpStatus.valueOf(httpResponse.getStatusCode().value()));
		}
		
		log.error("Unknown error occurred while processing PayPal create order response: {}", httpResponse);
		throw new ProcessingServiceException(ErrorCodeEnum.PAYPAL_PROVIDER_UNKNOWN_ERROR.getErrorCode(),
				ErrorCodeEnum.PAYPAL_PROVIDER_UNKNOWN_ERROR.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);

}
}
