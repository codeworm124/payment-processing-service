package com.hulkhiretech.payments.paypalprovider;

import lombok.Data;

@Data
public class PPCreateOrderReq {

	  private String currencyCode;
	  private Double amount;//later convert it into String
	  private String returnUrl;
	  private String cancelUrl;
	}
