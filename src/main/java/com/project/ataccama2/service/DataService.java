package com.project.ataccama2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.ataccama2.model.DBConnection;
import com.project.ataccama2.model.Tables;
import com.project.ataccama2.util.ConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.ResultSetMetaData;
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

    public Tables getTables(String name) throws Exception {
        DBConnection dbConnection = getDbConnection(name);
        JdbcTemplate jdbcTemplate = getJdbcTemplateByName(dbConnection);
        return queryForTables("SHOW TABLES;", jdbcTemplate);
    }

    public Map<String, List<String>> getColumns(String name, String tableName) throws Exception {
        // Example on how to handle possible SQL injection.
        validateTableName(name, tableName);

        DBConnection dbConnection = getDbConnection(name);
        JdbcTemplate jdbcTemplate = getJdbcTemplateByName(dbConnection);
        return queryForSingleColumnResult(
                "SELECT column_name FROM information_schema.columns WHERE table_name=? AND table_schema=?;",
                jdbcTemplate, tableName, dbConnection.getSchema());
    }

    private void validateTableName(String name, String tableName) throws Exception {
        if (!getTables(name).getTableNames().contains(tableName)) {
            throw new RuntimeException(name + " does not contain table: " + tableName);
        }
    }

    public Map<String, List<String>> previewData(String name, String tableName, Integer limit, Integer offset) throws Exception {
        DBConnection dbConnection = getDbConnection(name);
        JdbcTemplate jdbcTemplate = getJdbcTemplateByName(dbConnection);

        return queryForSingleColumnResult("SELECT * FROM " + tableName + " LIMIT ? OFFSET ?;", jdbcTemplate, limit, offset);
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

    private Map<String, List<String>> queryForSingleColumnResult(String query, JdbcTemplate jdbcTemplate, Object... args) {
        return jdbcTemplate.query(query, resultSet -> {
            Map<String, List<String>> res = new HashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                int colAmount = metaData.getColumnCount();
                for (int i = 1; i <= colAmount; ++i) {
                    if (res.containsKey(metaData.getColumnName(i))) {
                        res.get(metaData.getColumnName(i)).add(resultSet.getString(i));
                    } else {
                        List<String> singleValue = new ArrayList<>();
                        singleValue.add(resultSet.getString(i));
                        res.put(metaData.getColumnName(i), singleValue);
                    }
                }
            }
            return res;
        }, args);
    }

    private Tables queryForTables(String query, JdbcTemplate jdbcTemplate, Object... args) {
        return jdbcTemplate.query(query, resultSet -> {
            Tables tables = new Tables();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                int colAmount = metaData.getColumnCount();
                for (int i = 1; i <= colAmount; ++i) {
                    if (("TABLE_NAME").equals(metaData.getColumnName(i))) {
                        tables.addTable(resultSet.getString(i));
                    }
                }
            }
            return tables;
        }, args);
    }
}
