package com.hulkhiretech.payments.service.helper;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.dto.TransactionDto;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypalprovider.PPOrderResponse;

@Service
public class PPCaptureOrderHelper {

	public HttpRequest prepareHttpRequest(String txnReference, TransactionDto txnDto) {
		
		return null;
	}

	public PPOrderResponse handlePaypalResponse(ResponseEntity<String> responseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

}
