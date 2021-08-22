package com.project.ataccama2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.ataccama2.model.DBConnection;
import com.project.ataccama2.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataService {
    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private ConnectionManager connectionManager;

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
        UriComponentsBuilder queryBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/credentials/find")
                .queryParam("name", name);
        JsonNode credentials = restTemplate.getForEntity(queryBuilder.toUriString(), JsonNode.class, name).getBody();

        Iterator<Map.Entry<String, JsonNode>> responseFields = Objects.requireNonNull(credentials).fields();
        JdbcTemplate jdbcTemplate = connectionManager.getJdbcTemplate(new DBConnection(responseFields));
        List<String> result = jdbcTemplate.query("SHOW TABLES;", resultSet -> {
            List<String> res = new ArrayList<>();
            while (resultSet.next()) {
                res.add(resultSet.getString(1));
            }
            return res;
        });

        return result;
    }

    public Collection<String> getColumns(String databaseName, String tableName) {
        return Collections.singleton("");
    }

    public Collection<String> previewData(String name, String tableName) {
        return Collections.singleton("");
    }
}
