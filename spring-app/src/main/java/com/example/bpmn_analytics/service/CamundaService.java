package com.example.bpmn_analytics.service;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CamundaService {
    
    @Autowired
    private RuntimeService runtimeService;
    
    public String startProcess(String processDefinitionKey, String logId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("logId", logId);
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
            processDefinitionKey, 
            variables
        );
        
        return processInstance.getId();
    }
}
