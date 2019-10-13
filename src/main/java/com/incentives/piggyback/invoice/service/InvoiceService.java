package com.incentives.piggyback.invoice.service;

import com.incentives.piggyback.invoice.model.Invoice;
import com.incentives.piggyback.invoice.model.InvoiceRequest;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;


public interface InvoiceService {

	ResponseEntity<Invoice> getInvoiceById(Long id);

	Iterable<Invoice> getAllInvoice(HttpServletRequest request);

	ResponseEntity<Invoice> payInvoiceById(Long id, Invoice invoice);

	String emailInvoice(InvoiceRequest invoiceRequest);

	void generateInvoice();

	Iterable<Invoice> getOrderByPartnerId(String partnerId);
}