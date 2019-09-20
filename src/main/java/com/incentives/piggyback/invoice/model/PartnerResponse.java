package com.incentives.piggyback.invoice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartnerResponse {

	private String partnerId;
//	private String partnerName;
//	private String partnerDescription;
//	private String partnerWebHookAddress;
//	private String partnerOfficeAddress;
//	private String partnerMobile;
//	private List<String> userIds;
//	private Date createdDate;
//	private Date lastModifiedDate;
	private Integer isActive;
}