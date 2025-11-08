package com.example.bpmn_analytics.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessDefinitionDto {
    private String id;
    private String key;
    private String name;
    private int version;
    private String resource;



}
