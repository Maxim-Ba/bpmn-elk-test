package com.example.bpmn_analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.example.bpmn_analytics.repository")
public class BpmnAnalyticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BpmnAnalyticsApplication.class, args);
    }
}
