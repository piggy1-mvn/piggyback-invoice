package com.incentives.piggyback.invoice.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.incentives.piggyback.invoice.exception.InvoiceNotFoundException;
import com.incentives.piggyback.invoice.model.BroadcastResponse;
import com.incentives.piggyback.invoice.model.Invoice;
import com.incentives.piggyback.invoice.model.InvoiceRequest;
import com.incentives.piggyback.invoice.repository.InvoiceServiceRepository;
import com.incentives.piggyback.invoice.service.InvoiceService;
import com.incentives.piggyback.invoice.util.CommonUtility;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	private InvoiceServiceRepository invoiceServiceRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

	@Override
	public ResponseEntity<Invoice> getInvoiceById(Long id) {
		return ResponseEntity.ok(invoiceServiceRepository.findById(id).orElseThrow(()->new InvoiceNotFoundException(id)));
	}

	@Override
	public ResponseEntity<Invoice> payInvoiceById(Long id, Invoice invoice) {
		invoiceServiceRepository.findById(id).orElseThrow(()->new InvoiceNotFoundException(id));
		Invoice updatedInvoice = invoiceServiceRepository.save(invoice);
		return ResponseEntity.ok(updatedInvoice);
	}

	@Override
	public String emailInvoice(InvoiceRequest invoiceRequest) {
		String url = env.getProperty("notification.api.invoice");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(invoiceRequest, headers);
		ResponseEntity<BroadcastResponse> response = 
				restTemplate.exchange(url, HttpMethod.POST, 
						entity, BroadcastResponse.class);
		if (CommonUtility.isNullObject(response.getBody()) ||
				CommonUtility.isValidString(response.getBody().getData())) {
			log.error("email send failed with body {}", response.getBody());
		}
		return response.getBody().getData();
	}

}