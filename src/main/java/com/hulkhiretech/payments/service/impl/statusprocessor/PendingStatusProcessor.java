package com.hulkhiretech.payments.service.impl.statusprocessor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dao.interfaces.TransactionDao;
import com.hulkhiretech.payments.dto.TransactionDto;
import com.hulkhiretech.payments.entity.TransactionEntity;
import com.hulkhiretech.payments.service.Interface.TransactionStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PendingStatusProcessor implements TransactionStatusProcessor {

	private final TransactionDao transactionDao;
	private final ModelMapper modelMapper;
	
	@Override
	public TransactionDto processStatus(TransactionDto txnDto) {
		log.info("Processing Pending Status with ID: {}", txnDto);
		TransactionEntity txnEntity = modelMapper.map(txnDto, TransactionEntity.class);
		
		transactionDao.updateTransaction(txnEntity);
		
		return txnDto;
	}

}
