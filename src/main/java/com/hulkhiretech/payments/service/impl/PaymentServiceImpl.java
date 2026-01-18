package com.hulkhiretech.payments.service.impl;


import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dao.interfaces.TransactionDao;
import com.hulkhiretech.payments.dto.TransactionDto;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.exception.ProcessingServiceException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.paypalprovider.PPOrderResponse;
import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.InitiatePaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.PaymentService;
import com.hulkhiretech.payments.service.PaymentStatusService;
import com.hulkhiretech.payments.service.helper.PPCaptureOrderHelper;
import com.hulkhiretech.payments.service.helper.PPCreateOrderHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	
	private final ModelMapper modelMapper;
	private final PPCreateOrderHelper ppCreateOrderHelper;
	private final HttpServiceEngine httpServiceEngine;
	private final TransactionDao transactionDao;
	private final PPCaptureOrderHelper ppCaptureOrderHelper;	
	private final PaymentStatusService paymentStatusService;
	
	@Override
	public PaymentResponse createPayment(CreatePaymentRequest request) {
		log.info("Creating a new payment");
		
		
		TransactionDto txnDto = modelMapper.map(request, TransactionDto.class);
		log.info("Mapped CreatePaymentRequest to TransactionDto: {}", txnDto);
		
		int txnStatusId =1;// assuming 1 indicates 'Created' status
		String txnReference=generateUniqueTxnReference(); //for ever payment a unique reference
		
		txnDto.setTxnStatusId(txnStatusId);
		txnDto.setTxnReference(txnReference);
		
		TransactionDto transactionDto  =paymentStatusService.processPayment(txnDto);
		PaymentResponse paymentResponse=new PaymentResponse();
		paymentResponse.setTxnReference(transactionDto.getTxnReference());
		paymentResponse.setTxnStatusId(transactionDto.getTxnStatusId());
		log.info("Created PaymentResponse: {}", paymentResponse);
		
		return paymentResponse;
	}

	private String generateUniqueTxnReference() {
		return UUID.randomUUID().toString();
	}

	@Override
	public PaymentResponse initiatePayment(String txnReference,InitiatePaymentRequest initiatePaymentRequest) {
		
	   		log.info("initiatePayment with txnReference: {}", txnReference);
	   		
	   		TransactionEntity txnEntity= transactionDao.getTransactionByTxnReference(txnReference);
	   		log.info("Fetched TransactionEntity from DB: {}", txnEntity);
	   		
	   
	   		//use model mapper to convert this txnentity to dto
	   		TransactionDto txnDto=modelMapper.map(txnEntity, TransactionDto.class);
	   		
	   		//update initiated status
	   		txnDto.setTxnStatusId(2); //2 for initiated
	   		TransactionDto response  =paymentStatusService.processPayment(txnDto);
	   		log.info(txnReference+" after processing initiated status: {}", response);
	   		
	   		
	   		HttpRequest httpReq=ppCreateOrderHelper.prepareHttpRequest(txnReference, initiatePaymentRequest,txnDto);
	   		log.info("Prepared HttpRequest for PayPal Create Order: {}", httpReq);
	   		
	   		//code line from 88 to 93 should write in try catch block to handle http call exceptions
	   		//then inside catch block handle failed status update
	   		//and if not catch block executed then line from 98 to 101 execute   
	   		
	   		PPOrderResponse ppOrderResponse=null;
	   		
	   		try {
	   			ResponseEntity<String> httpResponse=httpServiceEngine.makeHttpCall(httpReq);
		   		log.info("Received response from PayPal Create Order: {}", httpResponse);
		   		
		   		ppOrderResponse = ppCreateOrderHelper.handlePaypalResponse(httpResponse);
		   		
		   		log.info("Processed PayPal Order Response: {}", ppOrderResponse);
		   		
	   		}
	   		catch(ProcessingServiceException ex) {
	   			log.error("Error during PayPal Create Order HTTP call: {}", ex.getMessage());
	   			
	   			txnDto.setTxnStatusId(6); //6 for failed
	   			txnDto.setErrorCode(ex.getErrorCode());
	   			txnDto.setErrorMessage(ex.getErrorMessage());
	   			response=paymentStatusService.processPayment(txnDto);
	   			 throw new ProcessingServiceException(ex.getErrorCode(), ex.getErrorMessage(), ex.getHttpStatus());
	   			
	   		}
	   		
	   		//TODO:update status to pending
	   		txnDto.setTxnStatusId(3); //3 for pending
	   		txnDto.setProviderReference(ppOrderResponse.getOrderId());
	   		response =paymentStatusService.processPayment(txnDto);
	   		log.info(txnReference+" after processing pending status: {}", response);
	   		
	   		PaymentResponse paymentResponse=new PaymentResponse();
	   		paymentResponse.setProviderReference(ppOrderResponse.getOrderId());
	   		paymentResponse.setRedirectUrl(ppOrderResponse.getRedirectUrl());
	   		
	   		paymentResponse.setTxnReference(txnDto.getTxnReference());
	   		paymentResponse.setTxnStatusId(txnDto.getTxnStatusId());
	   		log.info("Final PaymentResponse to be returned: {}", paymentResponse);
	   		
		return paymentResponse;
	}

	@Override
	public PaymentResponse capturePayment(String txnReference) {
		log.info("capturePayment with txnReference: {}", txnReference);
		
		TransactionEntity txnEntity=transactionDao.getTransactionByTxnReference(txnReference);
		log.info("Fetched TransactionEntity from DB: {}", txnEntity);
	    TransactionDto txnDto=modelMapper.map(txnEntity, TransactionDto.class);
		txnDto.setTxnStatusId(4); //4 for approved
		txnDto=paymentStatusService.processPayment(txnDto);//TODO:coding in ApprovedStatusProcessor
		log.info(txnReference+" after processing approved status: {}", txnDto);
		
		HttpRequest httpReq=ppCaptureOrderHelper.prepareHttpRequest(txnReference,txnDto);
		
		log.info("Prepared HttpRequest for PayPal Capture Order: {}", httpReq);
		PPOrderResponse ppOrderResponse=null;
		try {
			
			ResponseEntity<String> responseEntity=httpServiceEngine.makeHttpCall(httpReq);
			log.info("Received response from PayPal Capture Order: {}", responseEntity);
			ppOrderResponse = ppCaptureOrderHelper.handlePaypalResponse(responseEntity);
		}
		catch(ProcessingServiceException ex) {
			log.error("Error during PayPal Capture Order HTTP call: {}", ex.getMessage());
			throw ex; //custom exception
		}
		
		txnDto.setTxnStatusId(5); //5 for success
		log.info("PayPal Capture Order successful with Provider Reference: {}", ppOrderResponse.getOrderId());
		txnDto=paymentStatusService.processPayment(txnDto);
		log.info(txnReference+" after processing success status: {}", txnDto);
		
		PaymentResponse paymentResponse=new PaymentResponse();
		//paymentResponse.setProviderReference(response.getProviderReference()); it is dont pass because already set in txnDto
		paymentResponse.setTxnReference(txnDto.getTxnReference());
		paymentResponse.setTxnStatusId(txnDto.getTxnStatusId());
		log.info("Final PaymentResponse to be returned: {}", paymentResponse);
		
				return paymentResponse;
	}

}
