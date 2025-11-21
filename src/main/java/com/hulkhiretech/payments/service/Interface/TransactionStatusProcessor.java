package com.hulkhiretech.payments.service.Interface;

import com.hulkhiretech.payments.dto.TransactionDto;

public interface TransactionStatusProcessor {

	public TransactionDto processStatus(TransactionDto txnDto);
}
