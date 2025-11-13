package com.hulkhiretech.payments.service;

import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.InitiatePaymentRequest;

public interface PaymentService {

	public String createPayment(CreatePaymentRequest request);
	public String initiatePayment(String txnReference,InitiatePaymentRequest initiatePaymentRequest);
	public String capturePayment(String txnReference);
}
