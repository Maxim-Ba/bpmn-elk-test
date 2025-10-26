package com.example.bpmn_analytics.dto;

public class ProcessInstanceDto {
    private String id;
    private String processDefinitionId;
    private String businessKey;
    private boolean suspended;

    public ProcessInstanceDto() {}

    public ProcessInstanceDto(String id, String processDefinitionId, String businessKey, boolean suspended) {
        this.id = id;
        this.processDefinitionId = processDefinitionId;
        this.businessKey = businessKey;
        this.suspended = suspended;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getProcessDefinitionId() { return processDefinitionId; }
    public void setProcessDefinitionId(String processDefinitionId) { this.processDefinitionId = processDefinitionId; }
    
    public String getBusinessKey() { return businessKey; }
    public void setBusinessKey(String businessKey) { this.businessKey = businessKey; }
    
    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }
}
