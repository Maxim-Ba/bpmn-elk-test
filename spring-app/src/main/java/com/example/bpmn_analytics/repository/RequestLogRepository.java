package com.example.bpmn_analytics.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.bpmn_analytics.models.RequestLog;

public interface RequestLogRepository extends ElasticsearchRepository<RequestLog, String> {
}
