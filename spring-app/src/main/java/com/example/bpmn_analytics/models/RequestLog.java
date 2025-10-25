package com.example.bpmn_analytics.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "request_logs")
public class RequestLog {
   @Id
    private String id;
    private String method;
    private String endpoint;
    private String parameters;
    private LocalDateTime timestamp;
    private String processInstanceId;
    
    public RequestLog() {}
    
    public RequestLog(String method, String endpoint, String parameters) {
        this.method = method;
        this.endpoint = endpoint;
        this.parameters = parameters;
        this.timestamp = LocalDateTime.now();

    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getProcessInstanceId() { return processInstanceId; }
    public void setProcessInstanceId(String processInstanceId) { this.processInstanceId = processInstanceId; }
}
