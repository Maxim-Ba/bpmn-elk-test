package com.example.bpmn_analytics.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.bpmn_analytics.dto.HistoricProcessInstanceDto;
import com.example.bpmn_analytics.dto.ProcessDefinitionDto;
import com.example.bpmn_analytics.dto.ProcessInstanceDto;

@Service
public class CamundaRestService {
  private final RestTemplate restTemplate;
    
    @Value("${camunda.rest.url:http://camunda:8080/engine-rest}")
    private String camundaRestUrl;
    
    public CamundaRestService() {
        this.restTemplate = new RestTemplate();
    }
    
    public String startProcess(String processDefinitionKey, String logId, String initiator) {
        String url = camundaRestUrl + "/process-definition/key/" + processDefinitionKey + "/start";
        
        Map<String, Object> request = new HashMap<>();
        
        // Переменные процесса
        Map<String, Object> variables = new HashMap<>();
        variables.put("logId", Map.of("value", logId, "type", "String"));
        variables.put("initiator", Map.of("value", initiator, "type", "String"));
        request.put("variables", variables);
        
        // Заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            return (String) response.getBody().get("id");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start process: " + e.getMessage(), e);
        }
    }
    
    public List<ProcessInstanceDto> getActiveProcessInstances() {
        String url = camundaRestUrl + "/process-instance";
        
        try {
            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
            return Arrays.stream(response.getBody())
                    .map(this::mapToProcessInstanceDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get active processes: " + e.getMessage(), e);
        }
    }
    
    public List<HistoricProcessInstanceDto> getHistoricProcessInstances() {
        String url = camundaRestUrl + "/history/process-instance";
        
        try {
            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
            return Arrays.stream(response.getBody())
                    .map(this::mapToHistoricProcessInstanceDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get historic processes: " + e.getMessage(), e);
        }
    }
    public List<ProcessDefinitionDto> getProcessDefinitions() {
    String url = camundaRestUrl + "/process-definition";
    
    try {
        ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
        return Arrays.stream(response.getBody())
                .map(this::mapToProcessDefinitionDto)
                .collect(Collectors.toList());
    } catch (Exception e) {
        throw new RuntimeException("Failed to get process definitions: " + e.getMessage(), e);
    }
}

private ProcessDefinitionDto mapToProcessDefinitionDto(Map<String, Object> data) {
    ProcessDefinitionDto dto = new ProcessDefinitionDto();
    dto.setId((String) data.get("id"));
    dto.setKey((String) data.get("key"));
    dto.setName((String) data.get("name"));
    dto.setVersion((Integer) data.get("version"));
    dto.setResource((String) data.get("resource"));
    return dto;
}
private ProcessInstanceDto mapToProcessInstanceDto(Map<String, Object> data) {
        ProcessInstanceDto dto = new ProcessInstanceDto();
        dto.setId((String) data.get("id"));
        dto.setProcessDefinitionId((String) data.get("processDefinitionId"));
        dto.setBusinessKey((String) data.get("businessKey"));
        dto.setSuspended(data.get("suspended") != null ? (Boolean) data.get("suspended") : false);
        return dto;
    }
    
    private HistoricProcessInstanceDto mapToHistoricProcessInstanceDto(Map<String, Object> data) {
        HistoricProcessInstanceDto dto = new HistoricProcessInstanceDto();
        dto.setId((String) data.get("id"));
        dto.setProcessDefinitionId((String) data.get("processDefinitionId"));
        dto.setStartTime(parseDate(data.get("startTime")));
        dto.setEndTime(parseDate(data.get("endTime")));
        dto.setDurationInMillis(data.get("durationInMillis") != null ? 
            Long.valueOf(data.get("durationInMillis").toString()) : null);
        return dto;
    }
    
    private Date parseDate(Object dateStr) {
        if (dateStr == null) return null;
        // Парсинг даты из строки (формат Camunda)
        try {
            return new Date(java.sql.Timestamp.valueOf(
                dateStr.toString().replace("T", " ").replace("Z", "")
            ).getTime());
        } catch (Exception e) {
            return null;
        }
    }
    

}
