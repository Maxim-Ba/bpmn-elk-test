package com.example.bpmn_analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceDto {
    private String id;
    private String processDefinitionId;
    private String businessKey;
    private boolean suspended;





}
