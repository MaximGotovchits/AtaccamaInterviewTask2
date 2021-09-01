package com.project.ataccama2.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Tables {
    private Set<String> tableNames = new HashSet<>();

    public void addTable(String tableName) {
        tableNames.add(tableName);
    }
}
