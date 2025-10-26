package com.example.bpmn_analytics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bpmn_analytics.dto.SearchCriteria;
import com.example.bpmn_analytics.models.RequestLog;
import com.example.bpmn_analytics.service.ElasticsearchService;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private ElasticsearchService elasticsearchService;

    /**
     * Поиск логов по полю с частичным совпадением
     */
    @GetMapping("/search")
    public List<RequestLog> searchLogs(
            @RequestParam String field,
            @RequestParam String value) {
        return elasticsearchService.searchByFieldContains(field, value);
    }

    /**
     * Поиск по нескольким полям с условием И (AND)
     */
    @PostMapping("/search/multiple")
    public List<RequestLog> searchLogsByMultiple(
            @RequestBody List<SearchCriteria> searchCriteria) {
        return elasticsearchService.searchByMultipleFields(searchCriteria);
    }

    /**
     * Расширенный поиск с выбором оператора
     */
    @PostMapping("/search/multiple/{operator}")
    public List<RequestLog> searchLogsByMultipleWithOperator(
            @RequestBody List<SearchCriteria> searchCriteria,
            @PathVariable String operator) {
        return elasticsearchService.searchByMultipleFields(searchCriteria, operator);
    }

    /**
     * Полнотекстовый поиск по всем полям
     */
    @GetMapping("/search/text")
    public List<RequestLog> fullTextSearch(@RequestParam String query) {
        return elasticsearchService.fullTextSearch(query);
    }

    /**
     * Получить все логи (для тестирования)
     */
    @GetMapping("/all")
    public List<RequestLog> getAllLogs() {
        return elasticsearchService.getAllLogs();
    }
     /**
     * Получить все логи с пагинацией
     */
    @GetMapping("/all/paged")
    public Page<RequestLog> getAllLogsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return elasticsearchService.getAllLogsWithPagination(pageable);
    }

    /**
     * Поиск через нативный Elasticsearch REST API
     */
    @PostMapping("/search/multiple/native")
    public List<RequestLog> searchLogsByMultipleNative(
            @RequestBody List<SearchCriteria> searchCriteria,
            @RequestParam(defaultValue = "AND") String operator) {
        return elasticsearchService.searchByMultipleFieldsNative(searchCriteria, operator);
    }

    /**
     * Расширенный поиск с выбором типа запроса
     */
    @PostMapping("/search/multiple/advanced")
    public List<RequestLog> searchLogsAdvanced(
            @RequestBody List<SearchCriteria> searchCriteria,
            @RequestParam(defaultValue = "AND") String operator,
            @RequestParam(defaultValue = "WILDCARD") String queryType) {
        return elasticsearchService.advancedSearchNative(searchCriteria, operator, queryType);
    }
}
