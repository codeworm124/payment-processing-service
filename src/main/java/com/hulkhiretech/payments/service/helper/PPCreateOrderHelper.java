package com.hulkhiretech.payments.service.helper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypalprovider.PPCreateOrderReq;
import com.hulkhiretech.payments.pojo.InitiatePaymentRequest;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PPCreateOrderHelper {

	 private final JsonUtil jsonUtil;
	    
	public HttpRequest prepareHttpRequest(String txnReference, InitiatePaymentRequest initiatePaymentRequest) {
	
		//create header
		HttpHeaders headers=new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	   PPCreateOrderReq ppCreateOrderReq=new PPCreateOrderReq();
	   ppCreateOrderReq.setAmount(1.00);
	   ppCreateOrderReq.setCurrencyCode("USD");
	   ppCreateOrderReq.setReturnUrl(initiatePaymentRequest.getSuccessUrl());
	   ppCreateOrderReq.setCancelUrl(initiatePaymentRequest.getCancelUrl());
	   
	   //covert to json
	   String requestAsJson=jsonUtil.toJson(ppCreateOrderReq);
	   
	    
		//prepare httpRequest
	    HttpRequest httpRequest=new HttpRequest();
	    httpRequest.setHttpMethod(HttpMethod.POST);
	    httpRequest.setUrl("http:/localhost:8083/orders");
	    httpRequest.setBody(requestAsJson);
	    httpRequest.setHttpHeaders(headers);
	    return httpRequest;
		
	}

}
