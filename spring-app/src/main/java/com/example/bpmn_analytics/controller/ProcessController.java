package com.example.bpmn_analytics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bpmn_analytics.service.CamundaService;
import com.example.bpmn_analytics.service.ElasticsearchService;

@RestController
@RequestMapping("/api/process")
public class ProcessController {
    
    @Autowired
    private ElasticsearchService elasticsearchService;
    
    @Autowired
    private CamundaService camundaService;
    
    @PostMapping("/start")
    public ProcessStartResponse startProcess(
            @RequestParam String processDefinitionKey,
            @RequestParam String method,
            @RequestParam String endpoint,
            @RequestParam(required = false) String parameters) {
        
        // 1. Логируем запрос в Elasticsearch
        String logId = elasticsearchService.logRequest(method, endpoint, parameters);
        
        // 2. Запускаем процесс в Camunda
        String processInstanceId = camundaService.startProcess(processDefinitionKey, logId);
         // 3. Обновляем лог с ID процесса
        elasticsearchService.updateLogWithProcessInstanceId(logId, processInstanceId);
        
        return new ProcessStartResponse(processInstanceId, logId);
    }
    
    // DTO для ответа
    public static class ProcessStartResponse {
        private String processInstanceId;
        private String logId;
        
        public ProcessStartResponse(String processInstanceId, String logId) {
            this.processInstanceId = processInstanceId;
            this.logId = logId;
        }
        
        // Геттеры
        public String getProcessInstanceId() { return processInstanceId; }
        public String getLogId() { return logId; }
    }
}
