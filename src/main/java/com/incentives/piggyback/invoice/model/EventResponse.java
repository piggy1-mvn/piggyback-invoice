package com.incentives.piggyback.invoice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResponse {

    private String eventId;
    private String eventType;
    private String timeStamp;
    private String partnerId;
    private String sourceId;

}