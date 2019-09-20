package com.incentives.piggyback.invoice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerResponse {

    private String partnerId;
    private Integer isActive;
}