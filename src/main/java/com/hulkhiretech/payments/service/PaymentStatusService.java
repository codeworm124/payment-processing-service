package com.hulkhiretech.payments.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.dto.TransactionDto;
import com.hulkhiretech.payments.exception.ProcessingServiceException;
import com.hulkhiretech.payments.service.Interface.TransactionStatusProcessor;
import com.hulkhiretech.payments.service.factory.PaymentStatusFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusService {
	
	private final PaymentStatusFactory paymentStatusFactory;
	 
	  
      public TransactionDto processPayment(TransactionDto txnDto) {
    	  
    	  log.info("Processing payment status for TransactionDto: {}", txnDto);
    	  int statusId= txnDto.getTxnStatusId();
    	  TransactionStatusProcessor processor=paymentStatusFactory.getStatusProcessor(statusId);
    	  
    	  if(processor==null) {
			  log.error("No processor found for status ID: {}", statusId);
			  throw new ProcessingServiceException(ErrorCodeEnum.STATUS_PROCESSOR_NOT_FOUND.getErrorCode(),
					  ErrorCodeEnum.STATUS_PROCESSOR_NOT_FOUND.getErrorMessage(), 
					  HttpStatus.INTERNAL_SERVER_ERROR);
		  }
    	  
    	TransactionDto response= processor.processStatus(txnDto);
    	 return response;
    	  
      }

}
