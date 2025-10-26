package com.example.bpmn_analytics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bpmn_analytics.dto.HistoricProcessInstanceDto;
import com.example.bpmn_analytics.dto.ProcessDefinitionDto;
import com.example.bpmn_analytics.dto.ProcessInstanceDto;
import com.example.bpmn_analytics.service.CamundaRestService;
import com.example.bpmn_analytics.service.ElasticsearchService;

@RestController
@RequestMapping("/api/process")
public class ProcessController {
    
    @Autowired
    private ElasticsearchService elasticsearchService;
    
    @Autowired
    private CamundaRestService camundaRestService;

    @PostMapping("/start")
    public ProcessStartResponse startProcess(
            @RequestParam String processDefinitionKey,
            @RequestParam String method,
            @RequestParam String endpoint,
            @RequestParam(required = false) String parameters,
            @RequestParam(defaultValue = "demoUser") String initiator) {

        String logId = elasticsearchService.logRequest(method, endpoint, parameters);
        String processInstanceId = camundaRestService.startProcess(processDefinitionKey, logId, initiator);
        elasticsearchService.updateLogWithProcessInstanceId(logId, processInstanceId);
        
        return new ProcessStartResponse(processInstanceId, logId);
    }
    
    @GetMapping("/active")
    public List<ProcessInstanceDto> getActiveProcesses() {
        return camundaRestService.getActiveProcessInstances();
    }

    @GetMapping("/historic")
    public List<HistoricProcessInstanceDto> getHistoricProcesses() {
        return camundaRestService.getHistoricProcessInstances();
    }

    @GetMapping("/definitions")
    public List<ProcessDefinitionDto> getProcessDefinitions() {
        return camundaRestService.getProcessDefinitions();
    }
    public static class ProcessStartResponse {
        private String processInstanceId;
        private String logId;
        
        public ProcessStartResponse(String processInstanceId, String logId) {
            this.processInstanceId = processInstanceId;
            this.logId = logId;
        }
        public String getProcessInstanceId() {
            return processInstanceId;
        }

        public String getLogId() {
            return logId;
        }
    }

}
