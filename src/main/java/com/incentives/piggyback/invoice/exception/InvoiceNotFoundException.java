package com.incentives.piggyback.invoice.exception;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long id) {
        super("Could not find invoice with Id " + id);
    }

    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
