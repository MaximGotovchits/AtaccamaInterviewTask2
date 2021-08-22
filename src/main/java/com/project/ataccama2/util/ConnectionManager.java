package com.project.ataccama2.util;

import com.project.ataccama2.model.DBConnection;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<DBConnection, JdbcTemplate> connectionToDataSource = new ConcurrentHashMap<>();

//    private static final Integer CONNECTION_LIMIT = 1000;

    public JdbcTemplate getJdbcTemplate(DBConnection dbConnection) {
//        Can be modified in future to cache not more than 1000 of connections.
//        if (connectionToFreq.size() > CONNECTION_LIMIT) {
//            synchronized(this) {
//                while (connectionToFreq.size() > CONNECTION_LIMIT) {
//                    connectionToDataSource.remove(connectionToFreq.pollLast());
//                }
//            }
//        }
        return connectionToDataSource.computeIfAbsent(dbConnection, conn -> new JdbcTemplate(initDataSource(conn)));
    }

    private DataSource initDataSource(DBConnection dbConnection) {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .type(HikariDataSource.class)
                .url(constructUrl(dbConnection))
                .username(dbConnection.getUser())
                .password(dbConnection.getPassword())
                .build();
    }

    private String constructUrl(DBConnection dbConnection) {
        return dbConnection.getDbPrefix() + dbConnection.getHost() + ":" + dbConnection.getPort() + "/" + dbConnection.getSchema();
    }
}
