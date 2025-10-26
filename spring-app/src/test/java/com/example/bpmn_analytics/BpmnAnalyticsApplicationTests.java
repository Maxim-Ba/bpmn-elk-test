package com.example.bpmn_analytics;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.bpmn_analytics.repository.RequestLogRepository;
import com.example.bpmn_analytics.service.ElasticsearchService;

@SpringBootTest
class BpmnAnalyticsApplicationTests {

    @MockitoBean
    private RequestLogRepository requestLogRepository;

    @MockitoBean
    private ElasticsearchService elasticsearchService;


    @Test
    void contextLoads() {
        // Тест проверяет, что контекст Spring загружается корректно
    }
}
