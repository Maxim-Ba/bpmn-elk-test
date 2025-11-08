package com.example.bpmn_analytics.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class CamundaClient {
    
    private final RestTemplate restTemplate;

    @Value("${camunda.rest.url:http://camunda:8080/engine-rest}")
    private String camundaRestUrl;

    public CamundaClient() {
        this.restTemplate = new RestTemplate();
    }

    public <T> ResponseEntity<T> post(String url, Object request, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(request, headers);
        
        return restTemplate.exchange(camundaRestUrl + url, HttpMethod.POST, entity, responseType);
    }

    public <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        return restTemplate.getForEntity(camundaRestUrl + url, responseType);
    }

    public String buildUrl(String path) {
        return camundaRestUrl + path;
    }
}
