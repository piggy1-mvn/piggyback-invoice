package com.incentives.piggyback.invoice.model;

import java.util.Date;

import lombok.Data;

@Data
public class EventEntity {
    private String eventId;
    private String eventType;
    private Date timeStamp;
    private String partnerId;
    private String sourceId;
}