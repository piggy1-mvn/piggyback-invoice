package com.incentives.piggyback.invoice.util;

import com.incentives.piggyback.invoice.exception.InvoiceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvoiceNotFoundAdvice {
        @ResponseBody
        @ExceptionHandler(InvoiceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String userNotFoundHandler(InvoiceNotFoundException ex) {

                return ex.getMessage();
        }
}

