package com.example.bpmn_analytics.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorRequest {
   private String processInstanceId;
    private String failedActivity;
    private String errorTime;
    private String errorDetails;
}
