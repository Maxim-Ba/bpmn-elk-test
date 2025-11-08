package com.example.bpmn_analytics.service;

import com.example.bpmn_analytics.dto.HistoricProcessInstanceDto;
import com.example.bpmn_analytics.dto.ProcessDefinitionDto;
import com.example.bpmn_analytics.dto.ProcessInstanceDto;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class CamundaResponseMapper {

    public ProcessDefinitionDto mapToProcessDefinitionDto(Map<String, Object> data) {
        ProcessDefinitionDto dto = new ProcessDefinitionDto();
        dto.setId((String) data.get("id"));
        dto.setKey((String) data.get("key"));
        dto.setName((String) data.get("name"));
        dto.setVersion((Integer) data.get("version"));
        dto.setResource((String) data.get("resource"));
        return dto;
    }

    public ProcessInstanceDto mapToProcessInstanceDto(Map<String, Object> data) {
        ProcessInstanceDto dto = new ProcessInstanceDto();
        dto.setId((String) data.get("id"));
        dto.setProcessDefinitionId((String) data.get("processDefinitionId"));
        dto.setBusinessKey((String) data.get("businessKey"));
        dto.setSuspended(data.get("suspended") != null ? (Boolean) data.get("suspended") : false);
        return dto;
    }

    public HistoricProcessInstanceDto mapToHistoricProcessInstanceDto(Map<String, Object> data) {
        HistoricProcessInstanceDto dto = new HistoricProcessInstanceDto();
        dto.setId((String) data.get("id"));
        dto.setProcessDefinitionId((String) data.get("processDefinitionId"));
        dto.setStartTime(parseDate(data.get("startTime")));
        dto.setEndTime(parseDate(data.get("endTime")));
        dto.setDurationInMillis(data.get("durationInMillis") != null
                ? Long.valueOf(data.get("durationInMillis").toString()) : null);
        return dto;
    }

    private Date parseDate(Object dateStr) {
        if (dateStr == null) {
            return null;
        }
        try {
            return new Date(java.sql.Timestamp.valueOf(
                    dateStr.toString().replace("T", " ").replace("Z", "")
            ).getTime());
        } catch (Exception e) {
            return null;
        }
    }
}
