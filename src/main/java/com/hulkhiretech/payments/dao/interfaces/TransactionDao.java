package com.hulkhiretech.payments.dao.interfaces;

import org.springframework.stereotype.Repository;

import com.hulkhiretech.payments.entity.TransactionEntity;


public interface TransactionDao {

	public TransactionEntity createTransaction(TransactionEntity txnEntity);
	public TransactionEntity getTransactionByTxnReference(String txnReference);
	public void updateTransaction(TransactionEntity txnEntity);
}
	