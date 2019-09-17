package com.incentives.piggyback.invoice.model;

import lombok.Data;

@Data
public class InvoiceRequest {
	
	private String emailId;
	private String vendorDisplayName;
	private String subject;
	private String totalAmount;
}