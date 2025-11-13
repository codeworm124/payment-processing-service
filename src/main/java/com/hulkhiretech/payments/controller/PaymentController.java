package com.hulkhiretech.payments.controller;



import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.InitiatePaymentRequest;
import com.hulkhiretech.payments.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
 
	private final PaymentService paymentService;
	
	@PostMapping
	 public String createPayment(@RequestBody CreatePaymentRequest request) {
		log.info("Creating a new payment: {}", request);
		String response = paymentService.createPayment(request);
		log.info("Payment creation response: {}", response);
		return response;
	}
	
	@PostMapping("/{txnReference}/initiate")
	public String initiatePayment(@PathVariable String txnReference,
		 @RequestBody	InitiatePaymentRequest initiatePaymentRequest) {
		
		log.info("Received initiatePayment request for txnReference: {} with body: {}", txnReference, initiatePaymentRequest);
		
		log.info("Initiating payment process");
		
		//make APi call to paypal-provider to createOrder api
		/*
		 * 1)Preapare HttpRequest
		 * 2)Pass to HttpServiceEngine
		 * 3)Process Response
		 * */
		
		String response = paymentService.initiatePayment(txnReference, initiatePaymentRequest);
		
		
		log.info("Payment initiation response: {} ", response);
		return response;
	}
	
	@PostMapping("/{txnReference}/capture")
	public String capturePayment(@PathVariable String txnReference) {
		log.info("capturePayment with txnReference: {}", txnReference);
		log.info("Capturing payment");
		
		String response = paymentService.capturePayment(txnReference);
		log.info("Payment capture response: {}", response);
		return response;
	}
}
