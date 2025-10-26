package com.example.bpmn_analytics.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class HistoricProcessInstanceDto {
    private String id;
    private String processDefinitionId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date startTime;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date endTime;
    
    private Long durationInMillis;

    public HistoricProcessInstanceDto() {}

    public HistoricProcessInstanceDto(String id, String processDefinitionId, Date startTime, Date endTime, Long durationInMillis) {
        this.id = id;
        this.processDefinitionId = processDefinitionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationInMillis = durationInMillis;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getProcessDefinitionId() { return processDefinitionId; }
    public void setProcessDefinitionId(String processDefinitionId) { this.processDefinitionId = processDefinitionId; }
    
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    
    public Long getDurationInMillis() { return durationInMillis; }
    public void setDurationInMillis(Long durationInMillis) { this.durationInMillis = durationInMillis; }
}
