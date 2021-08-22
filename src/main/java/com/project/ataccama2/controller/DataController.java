package com.project.ataccama2.controller;

import com.project.ataccama2.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@RestController
public class DataController {
    @Autowired
    private DataService dataService;

    @GetMapping("/schemas")
    public @ResponseBody Collection<String> getSchemas() {
        return dataService.getSchemas();
    }

    @GetMapping("/tables")
    public @ResponseBody
    Map<String, List<String>> getTables(@RequestParam String name) throws Exception {
        return dataService.getTables(name);
    }

    @GetMapping("/columns")
    public @ResponseBody Map<String, List<String>> getColumns(
            @RequestParam String name,
            @RequestParam String tableName) throws Exception {
        return dataService.getColumns(name, tableName);
    }

    @GetMapping("/preview")
    public @ResponseBody Map<String, List<String>> getPreview(
            @RequestParam String name,
            @RequestParam String tableName,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) throws Exception {
        return dataService.previewData(name, tableName, limit, offset);
    }
}
