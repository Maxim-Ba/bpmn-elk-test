package com.example.bpmn_analytics.client;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CamundaClient {
    
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CamundaClient.class);
    @Value("${camunda.rest.url:http://camunda:8080/engine-rest}")
    private String camundaRestUrl;

    public CamundaClient() {
        this.restTemplate = new RestTemplate();
    }

    public <T> ResponseEntity<T> post(String url, Object request, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(request, headers);

        try {
        return restTemplate.exchange(camundaRestUrl + url, HttpMethod.POST, entity, responseType);
        } catch (Exception e) {
            logger.error("Error making POST request to {}: {}", url, e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<Object> sendMessage(String messageName, String processInstanceId) {
        Map<String, Object> messageRequest = new HashMap<>();
        messageRequest.put("messageName", messageName);
        messageRequest.put("processInstanceId", processInstanceId);
        logger.info("Sending message: {} for process instance: {}", messageName, processInstanceId);
        return post("/message", messageRequest, Object.class);
    }
    public <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        return restTemplate.getForEntity(camundaRestUrl + url, responseType);
    }

    public String buildUrl(String path) {
        return camundaRestUrl + path;
    }

}
