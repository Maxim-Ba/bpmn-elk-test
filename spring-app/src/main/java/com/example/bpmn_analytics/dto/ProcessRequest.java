package com.example.bpmn_analytics.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessRequest {
    private String processInstanceId;
    private String activity;
    private Integer counter;
    private String timestamp;
    
}
