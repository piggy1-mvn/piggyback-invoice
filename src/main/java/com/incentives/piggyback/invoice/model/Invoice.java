package com.incentives.piggyback.invoice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(schema = "invoicedb")
@Data
@NoArgsConstructor
public class Invoice {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="invoice_id")
                private Long invoice_id;

        @NotNull(message = "partnerId is mandatory")
        @Column(name="partnerId",length = 700)
        private String partnerId;

        @Column(name="lineItem")
        private String lineItem;

        @Column(name="amount")
        private int amount;

        @NotNull
        @Column(name="due_Date")
        private Date due_Date;

        @NotBlank(message = "status is mandatory")
        @Column(name="status")
        private String status;

    }




