package com.hulkhiretech.payments.service.impl.statusprocessor;

import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dto.TransactionDto;
import com.hulkhiretech.payments.service.Interface.TransactionStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FailedStatusProcessor implements TransactionStatusProcessor {

	@Override
	public TransactionDto processStatus(TransactionDto txnDto) {
		log.info("Processing Failed Status with ID: {}", txnDto);
		
		return txnDto;
	}

}
