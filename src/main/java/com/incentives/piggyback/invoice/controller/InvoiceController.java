package com.incentives.piggyback.invoice.controller;

import com.incentives.piggyback.invoice.model.Invoice;
import com.incentives.piggyback.invoice.service.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Validated
@RestController
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PutMapping("/invoice/{id}")
    public ResponseEntity<Invoice> payInvoice(@PathVariable Long id, @Valid @RequestBody Invoice invoice) {
        log.debug("Invoice Service: Received PUT request for updating invoice with partnerId." + id);
        return invoiceService.payInvoiceById(id, invoice);
    }

    @GetMapping("/invoice/")
    public Iterable<Invoice> getAllInvoice(HttpServletRequest request) {
        log.debug("Invoice Service: Received GET request for getting all Invoice");
        return invoiceService.getAllInvoice(request);
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        log.debug("Invoice Service: Received GET request for getting Invoice with partnerId." + id);
        return invoiceService.getInvoiceById(id);
    }

    @GetMapping("/invoice/partnerId")
    public ResponseEntity<Iterable<Invoice>> getInvoiceByPartnerId(@RequestParam("partnerId") String partnerId) {
        return ResponseEntity.ok(invoiceService.getOrderByPartnerId(partnerId));
    }

}
