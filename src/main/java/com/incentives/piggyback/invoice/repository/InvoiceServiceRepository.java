package com.incentives.piggyback.invoice.repository;

import com.incentives.piggyback.invoice.model.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceServiceRepository extends CrudRepository<Invoice, Long> {
    List<Invoice> findByPartnerId(String partnerId);
}

