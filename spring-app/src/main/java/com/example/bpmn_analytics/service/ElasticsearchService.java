package com.example.bpmn_analytics.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import com.example.bpmn_analytics.dto.SearchCriteria;
import com.example.bpmn_analytics.models.RequestLog;
import com.example.bpmn_analytics.repository.RequestLogRepository;

@Service
public class ElasticsearchService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private RequestLogRepository requestLogRepository;

    public String logRequest(String method, String endpoint, String parameters) {
        RequestLog log = new RequestLog(method, endpoint, parameters);
        RequestLog savedLog = requestLogRepository.save(log);
        return savedLog.getId();
    }

    public void updateLogWithProcessInstanceId(String logId, String processInstanceId) {
        requestLogRepository.findById(logId).ifPresent(log -> {
            log.setProcessInstanceId(processInstanceId);
            requestLogRepository.save(log);
        });
    }

    public List<RequestLog> searchByField(String field, String value) {
        Criteria criteria = new Criteria(field).is(value);
        CriteriaQuery query = new CriteriaQuery(criteria);

        SearchHits<RequestLog> searchHits = elasticsearchOperations.search(query, RequestLog.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public List<RequestLog> fullTextSearch(String searchText) {
        Criteria criteria = new Criteria("method").contains(searchText)
                .or(new Criteria("endpoint").contains(searchText))
                .or(new Criteria("parameters").contains(searchText))
                .or(new Criteria("processInstanceId").contains(searchText));
        CriteriaQuery query = new CriteriaQuery(criteria);

        SearchHits<RequestLog> searchHits = elasticsearchOperations.search(query, RequestLog.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * Получить все логи (исправленная версия)
     */
    public List<RequestLog> getAllLogs() {
        Iterable<RequestLog> iterable = requestLogRepository.findAll();
        List<RequestLog> result = new java.util.ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

    /**
     * Получить все логи с пагинацией
     */
    public org.springframework.data.domain.Page<RequestLog> getAllLogsWithPagination(
            org.springframework.data.domain.Pageable pageable) {
        return requestLogRepository.findAll(pageable);
    }

    /**
     * Поиск логов по полю с частичным совпадением (подстрока)
     *
     * @param field название поля
     * @param value значение для поиска (частичное совпадение)
     * @return список найденных логов
     */
    public List<RequestLog> searchByFieldContains(String field, String value) {
        Criteria criteria = new Criteria(field).contains(value);
        CriteriaQuery query = new CriteriaQuery(criteria);

        SearchHits<RequestLog> searchHits = elasticsearchOperations.search(query, RequestLog.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * Поиск по нескольким полям с условием И (AND)
     *
     * @param searchCriteriaList список критериев поиска {field, value}
     * @return список найденных логов
     */
    public List<RequestLog> searchByMultipleFields(List<SearchCriteria> searchCriteriaList) {
        if (searchCriteriaList == null || searchCriteriaList.isEmpty()) {
            return List.of();
        }

        Criteria criteria = null;

        for (SearchCriteria criteriaItem : searchCriteriaList) {
            if (criteria == null) {
                criteria = new Criteria(criteriaItem.getField()).contains(criteriaItem.getValue());
            } else {
                criteria = criteria.and(new Criteria(criteriaItem.getField()).contains(criteriaItem.getValue()));
            }
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<RequestLog> searchHits = elasticsearchOperations.search(query, RequestLog.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * Поиск по нескольким полям с условием ИЛИ (OR)
     *
     * @param searchCriteriaList список критериев поиска {field, value}
     * @return список найденных логов
     */
    public List<RequestLog> searchByMultipleFieldsOr(List<SearchCriteria> searchCriteriaList) {
        if (searchCriteriaList == null || searchCriteriaList.isEmpty()) {
            return List.of();
        }

        Criteria criteria = null;

        for (SearchCriteria criteriaItem : searchCriteriaList) {
            if (criteria == null) {
                criteria = new Criteria(criteriaItem.getField()).contains(criteriaItem.getValue());
            } else {
                criteria = criteria.or(new Criteria(criteriaItem.getField()).contains(criteriaItem.getValue()));
            }
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<RequestLog> searchHits = elasticsearchOperations.search(query, RequestLog.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * Расширенный поиск с поддержкой разных операторов
     *
     * @param searchCriteriaList список критериев
     * @param operator "AND" или "OR"
     * @return список найденных логов
     */
    public List<RequestLog> searchByMultipleFields(List<SearchCriteria> searchCriteriaList, String operator) {
        if ("OR".equalsIgnoreCase(operator)) {
            return searchByMultipleFieldsOr(searchCriteriaList);
        } else {
            return searchByMultipleFields(searchCriteriaList);
        }
    }
}
