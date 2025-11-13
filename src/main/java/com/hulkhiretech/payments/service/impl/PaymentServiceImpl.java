package com.hulkhiretech.payments.service.impl;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.InitiatePaymentRequest;
import com.hulkhiretech.payments.service.PaymentService;
import com.hulkhiretech.payments.service.helper.PPCreateOrderHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PPCreateOrderHelper ppCreateOrderHelper;
	private final HttpServiceEngine httpServiceEngine;
	@Override
	public String createPayment(CreatePaymentRequest request) {
		log.info("Creating a new payment");
		return "createPayment called from PaymentServiceImpl with request: " + request;
	}

	@Override
	public String initiatePayment(String txnReference,InitiatePaymentRequest initiatePaymentRequest) {
		
	   		log.info("initiatePayment with txnReference: {}", txnReference);
	   		
	   		HttpRequest httpReq=ppCreateOrderHelper.prepareHttpRequest(txnReference, initiatePaymentRequest);
	   		log.info("Prepared HttpRequest for PayPal Create Order: {}", httpReq);
	   		
	   		ResponseEntity<String> httpResponse=httpServiceEngine.makeHttpCall(httpReq);
	   		log.info("Received response from PayPal Create Order: {}", httpResponse);
	   		
		return "initiatePayment from PaymentServiceImpl with txnReference:" + txnReference 
				+" and initiatePaymentRequest: " + initiatePaymentRequest
				+" and paypalResponse: " + httpResponse;
	}

	@Override
	public String capturePayment(String txnReference) {
		log.info("capturePayment with txnReference: {}", txnReference);
				return "capturePayment from PaymentServiceImpl with txnReference: " + txnReference;
	}

}
