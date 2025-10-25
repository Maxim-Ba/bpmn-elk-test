package com.example.bpmn_analytics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bpmn_analytics.models.RequestLog;
import com.example.bpmn_analytics.repository.RequestLogRepository;


@Service
public class ElasticsearchService {

    @Autowired
    private RequestLogRepository requestLogRepository;

    public String logRequest(String method, String endpoint, String parameters) {
        RequestLog log = new RequestLog(method, endpoint, parameters);
        RequestLog savedLog = requestLogRepository.save(log);
        return savedLog.getId();
    }

    public void updateLogWithProcessInstanceId(String logId, String processInstanceId) {
        requestLogRepository.findById(logId).ifPresent(log -> {
            log.setProcessInstanceId(processInstanceId);
            requestLogRepository.save(log);
        });
    }
}
