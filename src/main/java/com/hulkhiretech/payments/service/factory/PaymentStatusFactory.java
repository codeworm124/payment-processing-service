package com.hulkhiretech.payments.service.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payments.service.Interface.TransactionStatusProcessor;
import com.hulkhiretech.payments.service.impl.statusprocessor.ApprovedStatusProcessor;
import com.hulkhiretech.payments.service.impl.statusprocessor.CreatedStatusProcessor;
import com.hulkhiretech.payments.service.impl.statusprocessor.FailedStatusProcessor;
import com.hulkhiretech.payments.service.impl.statusprocessor.InitiatedStatusProcessor;
import com.hulkhiretech.payments.service.impl.statusprocessor.PendingStatusProcessor;
import com.hulkhiretech.payments.service.impl.statusprocessor.SuccessStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusFactory {

	//dependency lookup to get beans dynamically
	 private final ApplicationContext applicationContext;
	 
	public TransactionStatusProcessor getStatusProcessor(int statusId) {
		log.info("Fetching TransactionStatusProcessor for status ID: {}", statusId);
		
		//todo:switch case for different status processors based on statusId
		switch(statusId) {
			case 1:
			return applicationContext.getBean(CreatedStatusProcessor.class);
			
			case 2:
				return applicationContext.getBean(InitiatedStatusProcessor.class);
				
			case 3:
				return applicationContext.getBean(PendingStatusProcessor.class);
				
			case 4:
				return applicationContext.getBean(ApprovedStatusProcessor.class);
				
			case 5:
				return applicationContext.getBean(SuccessStatusProcessor.class);
				
			case 6:
				return applicationContext.getBean(FailedStatusProcessor.class);
			default:
				log.warn("No TransactionStatusProcessor found for status ID: {}", statusId);
		}
		return null; 
	}
}
