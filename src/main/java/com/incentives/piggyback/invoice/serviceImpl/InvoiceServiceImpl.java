package com.incentives.piggyback.invoice.serviceImpl;

import static com.incentives.piggyback.invoice.util.constants.Constant.INVOICE_PARTNER_BILL_LINE_ITEM;
import static com.incentives.piggyback.invoice.util.constants.Constant.OFFER_CREATED_EVENT;
import static com.incentives.piggyback.invoice.util.constants.Constant.ORDER_OPTIMIZED_EVENT;
import static com.incentives.piggyback.invoice.util.constants.Constant.RATE_PER_OFFER_CREATED;
import static com.incentives.piggyback.invoice.util.constants.Constant.RATE_PER_ORDER_OPTIMIZED;
import static com.incentives.piggyback.invoice.util.constants.PaymentStatus.PENDING;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.incentives.piggyback.invoice.exception.InvoiceNotFoundException;
import com.incentives.piggyback.invoice.model.BroadcastResponse;
import com.incentives.piggyback.invoice.model.EventResponse;
import com.incentives.piggyback.invoice.model.Invoice;
import com.incentives.piggyback.invoice.model.InvoiceRequest;
import com.incentives.piggyback.invoice.model.PartnerResponse;
import com.incentives.piggyback.invoice.repository.InvoiceServiceRepository;
import com.incentives.piggyback.invoice.service.InvoiceService;
import com.incentives.piggyback.invoice.util.CommonUtility;

import javax.servlet.http.HttpServletRequest;

@Component
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
		return ResponseEntity.ok(invoiceServiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException(id)));
	}

	public Iterable<Invoice> getAllInvoice(HttpServletRequest request) {
		if(null!=request && null != request.getHeader("Authorization")){
			if(isAuthorized(request.getHeader("Authorization"))){
				return invoiceServiceRepository.findAll();
			}
		}else{
			throw new InvoiceNotFoundException("InvoiceService: User not Authorized to access invoice");
		}
		throw new InvoiceNotFoundException("Invoice Service:Authorization header is empty");
	}

	@Override
	public ResponseEntity<Invoice> payInvoiceById(Long id, Invoice invoice) {
		invoiceServiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException(id));
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


	@Scheduled(cron = "${cron.pattern}")
	@Override
	public void generateInvoice() {

		int partnerBillAmount = 0;
		log.info("Invoice Service:Started generating Invoice for partners");
		String url = env.getProperty("partner.api.invoice");
		assert url != null;
		ResponseEntity<PartnerResponse[]> response =
				restTemplate.getForEntity(url, PartnerResponse[].class);
		if (CommonUtility.isNullObject(response.getBody())) {
			log.error("Invoice Service: No active Partner Id available", response.getBody());
		}

		List<String> activePartnerIds = new ArrayList<String>();
		for (PartnerResponse partnerResponse : response.getBody()) {
			if (partnerResponse.getIsActive() == 1) {
				activePartnerIds.add(partnerResponse.getPartnerId());
			}
		}
		for (String partnerId : activePartnerIds) {
			int offerCreatedEventCount = getTotalEventsForPartnerId(OFFER_CREATED_EVENT, partnerId);
			int orderOptimizedEventCount = getTotalEventsForPartnerId(ORDER_OPTIMIZED_EVENT, partnerId);
			partnerBillAmount = (offerCreatedEventCount * RATE_PER_OFFER_CREATED) + (orderOptimizedEventCount * RATE_PER_ORDER_OPTIMIZED);
			log.info("Calculated bill amount is {}",partnerBillAmount);
			if (partnerBillAmount != 0) {
				saveInvoice(partnerBillAmount, partnerId);
			}
			log.info("partnerBillAmount generated value" + partnerBillAmount);
		}

	}

	@Override
	public Iterable<Invoice> getOrderByPartnerId(String partnerId) {
		log.info("Invoice Service: Started getting invoice by invoice Id");
		return invoiceServiceRepository.findByPartnerId(partnerId);
	}

	private void saveInvoice(int partnerBillAmount, String partnerId) {
		log.info("partnerBillAmount started saving" + partnerBillAmount);
		Invoice invoice = new Invoice();
		invoice.setAmount(partnerBillAmount);
		invoice.setPartnerId(partnerId);
		invoice.setLineItem(INVOICE_PARTNER_BILL_LINE_ITEM);
		invoice.setStatus(PENDING.toString());
		invoice.setDue_Date(getDueDate());
		invoiceServiceRepository.save(invoice);
		log.info("partnerBillAmount saved" + partnerBillAmount);
	}

	private int getTotalEventsForPartnerId(String eventType, String partnerId) {
		log.info("getTotalEventsForPartnerId for partner Id" + partnerId);
		String eventUrl = env.getProperty("event.api.invoice");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(eventUrl)
				.queryParam("partnerId",partnerId);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		log.info("getTotalEventsForPartnerId sent total Partner uri {}",builder.toUriString());
		ResponseEntity<EventResponse> eventResponse =
				restTemplate.exchange(builder.toUriString()+"&eventType="+eventType, HttpMethod.GET,
						entity, EventResponse.class);
		if (CommonUtility.isNullObject(eventResponse.getBody())) {
			log.error("Invoice Service: No events for Partner Id available", eventResponse.getBody());
		}
		log.info("getTotalEventsForPartnerId sent total Partner count" + eventResponse.getBody().getEventEntity().size());
		return eventResponse.getBody().getEventEntity().size();
	}

	private Date getDueDate() {
		Date todaysDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(todaysDate);
		c.add(Calendar.MONTH, 1);
		Date dueDate = c.getTime();
		return dueDate;
	}

	private boolean isAuthorized(String accessToken) {
		log.info("Partner Service: User token validation from user service");
		String url = env.getProperty("users.api.userValid");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Authorization", accessToken);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<Integer> response =
				restTemplate.exchange(url, HttpMethod.HEAD,
						entity,Integer.class);
		if (CommonUtility.isNullObject(response.getStatusCode())){
			throw new InvoiceNotFoundException("User not authorized to create partner");
		}else if(response.getStatusCodeValue()==200){
			return true;
		}
		else
			return false;

	}

}