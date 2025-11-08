package com.example.bpmn_analytics.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import com.example.bpmn_analytics.client.CamundaClient;
import com.example.bpmn_analytics.dto.HistoricProcessInstanceDto;
import com.example.bpmn_analytics.dto.ProcessDefinitionDto;
import com.example.bpmn_analytics.dto.ProcessInstanceDto;

@Service
@RequiredArgsConstructor
public class CamundaRestService {

    private final CamundaClient camundaClient;
    private final CamundaResponseMapper responseMapper;
    private final ProcessVariableBuilder variableBuilder;

    public String startProcess(String processDefinitionKey, String logId, String initiator) {
        String url = "/process-definition/key/" + processDefinitionKey + "/start";
        Map<String, Object> request = variableBuilder.buildStartProcessVariables(logId, initiator);

        try {
            ResponseEntity<Map> response = camundaClient.post(url, request, Map.class);
            return (String) response.getBody().get("id");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start process: " + e.getMessage(), e);
        }
    }

    public List<ProcessInstanceDto> getActiveProcessInstances() {
        return getProcessInstancesInternal("/process-instance")
                .stream()
                .map(responseMapper::mapToProcessInstanceDto)
                .collect(Collectors.toList());
    }

    public List<HistoricProcessInstanceDto> getHistoricProcessInstances() {
        return getProcessInstancesInternal("/history/process-instance")
                .stream()
                .map(responseMapper::mapToHistoricProcessInstanceDto)
                .collect(Collectors.toList());
    }

    public List<ProcessDefinitionDto> getProcessDefinitions() {
        return getProcessInstancesInternal("/process-definition")
                .stream()
                .map(responseMapper::mapToProcessDefinitionDto)
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getProcessInstancesInternal(String url) {
        try {
            ResponseEntity<Map[]> response = camundaClient.get(url, Map[].class);
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get processes from " + url + ": " + e.getMessage(), e);
        }
    }
}
