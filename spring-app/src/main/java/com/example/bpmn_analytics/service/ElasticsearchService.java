package com.example.bpmn_analytics.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
@Service
public class ElasticsearchService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    @Autowired
    private RequestLogRepository requestLogRepository;
    @Autowired
    private ElasticsearchClient elasticsearchClient;


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

    /**
     * Поиск по нескольким полям с использованием нативного Elasticsearch REST
     * API
     */
    public List<RequestLog> searchByMultipleFieldsNative(List<SearchCriteria> searchCriteriaList, String operator) {
        try {
            // Создаем bool query
            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

            // Добавляем условия в зависимости от оператора
            for (SearchCriteria criteria : searchCriteriaList) {
                Query query = buildQueryForCriteria(criteria);

                if ("OR".equalsIgnoreCase(operator)) {
                    boolQueryBuilder.should(query);
                } else {
                    boolQueryBuilder.must(query);
                }
            }

            // Строим поисковый запрос
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("request_logs")
                    .query(q -> q.bool(boolQueryBuilder.build()))
                    .size(1000)
            );

            // Выполняем запрос с использованием Map для промежуточного парсинга
            SearchResponse<Object> response = elasticsearchClient.search(searchRequest, Object.class);

            // Вручную преобразуем результат в RequestLog
            return response.hits().hits().stream()
                    .map(hit -> convertToRequestLog(hit.source()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при поиске в Elasticsearch", e);
        }
    }

    /**
     * Конвертирует Map в RequestLog
     */
    private RequestLog convertToRequestLog(Object source) {
        try {
            if (source instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> sourceMap = (Map<String, Object>) source;

                RequestLog log = new RequestLog();
                log.setId((String) sourceMap.get("id"));
                log.setMethod((String) sourceMap.get("method"));
                log.setEndpoint((String) sourceMap.get("endpoint"));
                log.setParameters((String) sourceMap.get("parameters"));
                log.setProcessInstanceId((String) sourceMap.get("processInstanceId"));

                // Обработка timestamp
                Object timestamp = sourceMap.get("timestamp");
                if (timestamp instanceof String) {
                    log.setTimestamp(Instant.parse((String) timestamp));
                } else if (timestamp instanceof Long) {
                    log.setTimestamp(Instant.ofEpochMilli((Long) timestamp));
                }

                return log;
            }
            return null;
        } catch (Exception e) {
            // Логируем ошибку, но не прерываем выполнение
            System.err.println("Ошибка при конвертации: " + e.getMessage());
            return null;
        }
    }

    /**
     * Построение query для одного критерия поиска
     */
    private Query buildQueryForCriteria(SearchCriteria criteria) {

        String fieldName = criteria.getField() + ".keyword";

        String wildcardValue = "*" + escapeWildcardCharacters(criteria.getValue()) + "*";

        return Query.of(q -> q
                .wildcard(w -> w
                .field(fieldName)
                .value(wildcardValue)
                )
        );
    }

    /**
     * Экранирование специальных символов для wildcard запроса
     */
    private String escapeWildcardCharacters(String value) {
        // Экранируем специальные символы: *, ?, \
        return value.replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("?", "\\?");
    }

    /**
     * Расширенный поиск с поддержкой разных типов запросов
     */
    public List<RequestLog> advancedSearchNative(List<SearchCriteria> searchCriteriaList, String operator, String queryType) {
        try {
            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

            for (SearchCriteria criteria : searchCriteriaList) {
                Query query;

                switch (queryType.toUpperCase()) {
                    case "MATCH":
                        // Точное совпадение (с анализом текста)
                        query = Query.of(q -> q
                                .match(m -> m
                                .field(criteria.getField())
                                .query(criteria.getValue())
                                )
                        );
                        break;
                    case "TERM":
                        // Точное совпадение (без анализа)
                        query = Query.of(q -> q
                                .term(t -> t
                                .field(criteria.getField() + ".keyword")
                                .value(criteria.getValue())
                                )
                        );
                        break;
                    case "WILDCARD":
                    default:
                        // Поиск по подстроке (по умолчанию)
                        query = buildQueryForCriteria(criteria);
                        break;
                }

                if ("OR".equalsIgnoreCase(operator)) {
                    boolQueryBuilder.should(query);
                } else {
                    boolQueryBuilder.must(query);
                }
            }

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("request_logs")
                    .query(q -> q.bool(boolQueryBuilder.build()))
                    .size(1000)
            );

            SearchResponse<RequestLog> response = elasticsearchClient.search(searchRequest, RequestLog.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при расширенном поиске в Elasticsearch", e);
        }
    }
}
