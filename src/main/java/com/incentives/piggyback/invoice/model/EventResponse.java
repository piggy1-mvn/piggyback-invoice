package com.incentives.piggyback.invoice.model;

import java.util.List;

import lombok.Data;

@Data
public class EventResponse {

	private List<EventEntity> eventEntity;
}