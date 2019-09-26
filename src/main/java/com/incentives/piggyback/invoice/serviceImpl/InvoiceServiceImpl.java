package com.incentives.piggyback.invoice.serviceImpl;

import com.incentives.piggyback.invoice.model.*;
import org.apache.commons.lang3.time.DateUtils;
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

import com.incentives.piggyback.invoice.exception.InvoiceNotFoundException;
import com.incentives.piggyback.invoice.repository.InvoiceServiceRepository;
import com.incentives.piggyback.invoice.service.InvoiceService;
import com.incentives.piggyback.invoice.util.CommonUtility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.incentives.piggyback.invoice.util.constants.Constant.*;
import static com.incentives.piggyback.invoice.util.constants.PaymentStatus.PENDING;

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

        double partnerBillAmount = 0.0;
        log.debug("Started generating Invoice for partners");
        String url = env.getProperty("partner.api.invoice");
        assert url != null;
        ResponseEntity<PartnerResponse[]> response =
                restTemplate.getForEntity(url, PartnerResponse[].class);
        if (CommonUtility.isNullObject(response.getBody())) {
            log.error("email send failed with body {}", response.getBody());
        }

        List<String> activePartnerIds = new ArrayList<String>();
        for (PartnerResponse partnerResponse : response.getBody()) {
            if (partnerResponse.getIsActive() == 1) {
                activePartnerIds.add(partnerResponse.getPartnerId());
            }
        }

        for (String partnerId : activePartnerIds) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date todaysDate = new Date();
            int offerCreatedEventCount = getTotalEventsForPartnerId(OFFER_CREATED_EVENT, partnerId, df.format(todaysDate));
            int orderOptimizedEventCount = getTotalEventsForPartnerId(ORDER_OPTIMIZED_EVENT, partnerId, df.format(todaysDate));
            partnerBillAmount = (offerCreatedEventCount * RATE_PER_OFFER_CREATED) + (orderOptimizedEventCount * RATE_PER_ORDER_OPTIMIZED);
            saveInvoice(partnerBillAmount, partnerId);
        }

    }

    private void saveInvoice(double partnerBillAmount, String partnerId) {
        Invoice invoice = new Invoice();
        invoice.setAmount(partnerBillAmount);
        invoice.setPartnerId(partnerId);
        invoice.setLineItem(INVOICE_PARTNER_BILL_LINE_ITEM);
        invoice.setStatus(PENDING.toString());
        invoice.setDue_Date(getDueDate());
        invoiceServiceRepository.save(invoice);
    }

    private int getTotalEventsForPartnerId(String eventType, String partnerId, String timestamp) {
        String eventUrl = env.getProperty("event.api.invoice") + "?eventType=" + eventType + "&partnerId=" +
                partnerId + "&timeStamp=" + timestamp;
        ResponseEntity<EventResponse[]> eventResponse =
                restTemplate.getForEntity(eventUrl, EventResponse[].class);
        if (CommonUtility.isNullObject(eventResponse.getBody())) {
            log.error("email send failed with body {}", eventResponse.getBody());
        }
        return eventResponse.getBody().length;
    }

    private Date getDueDate() {
        Date todaysDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(todaysDate);
        c.add(Calendar.MONTH, 1);
        Date dueDate = c.getTime();
        return dueDate;
    }


}