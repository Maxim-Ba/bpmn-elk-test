package com.example.bpmn_analytics.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bpmn_analytics.client.CamundaClient;
import com.example.bpmn_analytics.dto.ErrorRequest;
import com.example.bpmn_analytics.dto.HistoricProcessInstanceDto;
import com.example.bpmn_analytics.dto.ProcessDefinitionDto;
import com.example.bpmn_analytics.dto.ProcessInstanceDto;
import com.example.bpmn_analytics.dto.ProcessRequest;
import com.example.bpmn_analytics.service.CamundaRestService;
import com.example.bpmn_analytics.service.ElasticsearchService;

@RestController
@RequestMapping("/api/process")
public class ProcessController {

    private static final Logger logger = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private CamundaRestService camundaRestService;
    @Autowired
    private CamundaClient camundaClient;

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

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> handleProcess(@RequestBody ProcessRequest request) {
        logger.info("/process: Reciev from  Camunda: {}", request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Engine", "Camunda");
        try {
            // Ваша бизнес-логика
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Процесс обработан успешно");
            response.put("timestamp", new Date());
            response.put("receivedData", request);

            response.put("processInstanceId", request.getProcessInstanceId());
            response.put("activity", request.getActivity());
            response.put("result", "COMPLETED");

            logger.info("Send response to Camunda: {}", response);
            return new ResponseEntity<>(response, headers, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("result", "ERROR");
            errorResponse.put("code", 500);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping(value = "/second-task", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> handleSecondTask(@RequestBody Map<String, Object> request) {
        logger.info("/second-task: Second task: {}", request);
        return ResponseEntity.ok(Map.of("status", "COMPLETED", "task", "second"));
    }

    @PostMapping("/errors")
    public ResponseEntity<Void> handleError(@RequestBody ErrorRequest error) {
        logger.info("Receive error from Camunda: {}", error);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subprocess")
    public ResponseEntity<Map<String, Object>> handleSubprocess(@RequestBody Map<String, Object> request) {
        logger.info("Subprocess: {}", request);
        return ResponseEntity.ok(Map.of("subprocessStatus", "COMPLETED"));
    }

    @PostMapping("/condition")
    public ResponseEntity<Map<String, Object>> handleConditionCheck() {
        logger.info("/condition: Checking condition for gateway");

        // Возвращаем случайное булево значение для выбора пути
        boolean conditionResult = Math.random() > 0.5;

        Map<String, Object> response = new HashMap<>();
        response.put("conditionResult", conditionResult);
        response.put("timestamp", new Date());
        response.put("message", conditionResult ? "Условие истинно - идем по верхнему пути" : "Условие ложно - идем по нижнему пути");

        logger.info("Condition check result: {}", conditionResult);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/properties")
    public ResponseEntity<Map<String, Object>> handlePropertiesTask(@RequestBody Map<String, Object> request) {
        logger.info("/properties: Received properties task request: {}", request);

        String processInstanceId = (String) request.get("processInstanceId");
        logger.info("Processing properties for process instance: {}", processInstanceId);

        // Проверяем активные активности процесса
        try {
            String activitiesUrl = "/process-instance/" + processInstanceId + "/activity-instances";
            ResponseEntity<Object> activitiesResponse = camundaClient.get(activitiesUrl, Object.class);
            logger.info("Current active activities: {}", activitiesResponse.getBody());
        } catch (Exception e) {
            logger.error("Failed to get active activities", e);
        }

        // Запускаем отправку BreakLoopMessage через 5 секунд
        new Thread(() -> {
            try {
                logger.info("Waiting 5 seconds before sending BreakLoopMessage...");
                Thread.sleep(5000);

                // Проверяем, находится ли процесс еще в первом цикле
                try {
                    String activitiesUrl = "/process-instance/" + processInstanceId + "/activity-instances";
                    ResponseEntity<Object> activitiesResponse = camundaClient.get(activitiesUrl, Object.class);
                    logger.info("Current state before sending message: {}", activitiesResponse.getBody());
                } catch (Exception e) {
                    logger.error("Failed to check process state", e);
                }

                Map<String, Object> messageRequest = new HashMap<>();
                messageRequest.put("messageName", "breakLoopMessage");
                messageRequest.put("processInstanceId", processInstanceId);

                logger.info("Attempting to send breakLoopMessage to process instance: {}", processInstanceId);
                ResponseEntity<Object> response = camundaClient.post("/message", messageRequest, Object.class);
                logger.info("BreakLoopMessage sent successfully. Response: {}", response.getStatusCode());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting to send BreakLoopMessage", e);
            } catch (Exception e) {
                logger.error("Failed to send BreakLoopMessage", e);
            }
        }).start();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "PROPERTIES_PROCESSED");
        response.put("breakLoopScheduled", true);
        response.put("scheduledTime", new Date());

        return ResponseEntity.ok(response);
    }

}
