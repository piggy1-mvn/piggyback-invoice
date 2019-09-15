package com.incentives.piggyback.invoice.serviceImpl;

import com.incentives.piggyback.invoice.exception.InvoiceNotFoundException;
import com.incentives.piggyback.invoice.model.Invoice;
import com.incentives.piggyback.invoice.repository.InvoiceServiceRepository;
import com.incentives.piggyback.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceServiceRepository invoiceServiceRepository;

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

}

