package com.project.ataccama2.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DataService {
    @Autowired
    private final RestTemplate restTemplate;

    public DataService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Collection<String> getSchemas() {
        return Objects.requireNonNull(restTemplate.getForEntity("http://localhost:8081/credentials/list", JsonNode.class)
                .getBody()) // Better use shared model.
                .findValuesAsText("databaseName")
                .stream()
                .distinct() // More efficient to do it in SQL query.
                .collect(Collectors.toList());
    }

    public Collection<String> getTables(String name) {
        return Collections.singleton("");
    }

    public Collection<String> getColumns(String name, String tableName) {
        return Collections.singleton("");
    }

    public Collection<String> previewData(String name, String tableName) {
        return Collections.singleton("");
    }
}
