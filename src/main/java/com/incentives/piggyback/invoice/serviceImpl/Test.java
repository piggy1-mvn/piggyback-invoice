//package com.incentives.piggyback.invoice.serviceImpl;
//
//import java.util.Date;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import com.incentives.piggyback.invoice.model.EventResponse;
//
//public class Test {
//
//	public static void main(String[] args) {
//		try {
//		String eventUrl = "http://108.59.87.44:8082/events";
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
//		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(eventUrl)
//				.queryParam("eventType","Offers Events Created")
//				.queryParam("partnerId","89e9e9fb-5c24-4cab-a833-95b5c6215cf0")
//				.queryParam("timestamp",new Date().getTime());
//		HttpEntity<?> entity = new HttpEntity<>(headers);
//		RestTemplate restTemplate =new RestTemplate();
//		ResponseEntity<EventResponse[]> eventResponse =
//				restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
//						entity, EventResponse[].class);
//		System.out.println(eventResponse.getBody());
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//	}
//}
