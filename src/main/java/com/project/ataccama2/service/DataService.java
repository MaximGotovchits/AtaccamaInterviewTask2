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

    public Collection<String> getTables(String name) throws Exception {
        DBConnection dbConnection = getDbConnection(name);
        JdbcTemplate jdbcTemplate = getJdbcTemplateByName(dbConnection);
        return queryForSingleColumnResult("SHOW TABLES;", jdbcTemplate);
    }

    public Collection<String> getColumns(String name, String tableName) throws Exception {
        DBConnection dbConnection = getDbConnection(name);
        JdbcTemplate jdbcTemplate = getJdbcTemplateByName(dbConnection);
        return queryForSingleColumnResult(
                "SELECT column_name FROM information_schema.columns WHERE table_name=? AND table_schema=?;",
                jdbcTemplate, tableName, dbConnection.getSchema());
    }

    private JdbcTemplate getJdbcTemplateByName(DBConnection dbConnection) throws Exception {
        return connectionManager.getJdbcTemplate(dbConnection);
    }

    private DBConnection getDbConnection(String name) {
        UriComponentsBuilder queryBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/credentials/find")
                .queryParam("name", name);
        JsonNode credentials = restTemplate.getForEntity(queryBuilder.toUriString(), JsonNode.class, name).getBody();

        Iterator<Map.Entry<String, JsonNode>> responseFields = Objects.requireNonNull(credentials).fields();

        return new DBConnection(responseFields);
    }

    private Collection<String> queryForSingleColumnResult(String query, JdbcTemplate jdbcTemplate, Object... args) {
        return jdbcTemplate.query(query, resultSet -> {
            List<String> res = new ArrayList<>();
            while (resultSet.next()) {
                res.add(resultSet.getString(1));
            }
            return res;
        }, args);
    }

    public Collection<String> previewData(String name, String tableName) {
        return Collections.singleton("");
    }
}
