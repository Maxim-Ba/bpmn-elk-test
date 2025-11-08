package com.example.bpmn_analytics.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessVariableBuilder {

    public Map<String, Object> buildStartProcessVariables(String logId, String initiator) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("logId", createVariableValue(logId, "String"));
        variables.put("initiator", createVariableValue(initiator, "String"));
        
        Map<String, Object> request = new HashMap<>();
        request.put("variables", variables);
        return request;
    }

    private Map<String, Object> createVariableValue(String value, String type) {
        Map<String, Object> variable = new HashMap<>();
        variable.put("value", value);
        variable.put("type", type);
        return variable;
    }
}
