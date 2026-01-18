package com.hulkhiretech.payments.service;

import com.hulkhiretech.payments.pojo.CreatePaymentRequest;
import com.hulkhiretech.payments.pojo.InitiatePaymentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;

public interface PaymentService {

	public PaymentResponse createPayment(CreatePaymentRequest request);
	public PaymentResponse initiatePayment(String txnReference,InitiatePaymentRequest initiatePaymentRequest);
	public PaymentResponse capturePayment(String txnReference);
}
