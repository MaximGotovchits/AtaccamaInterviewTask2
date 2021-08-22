package com.project.ataccama2.controller;

import com.project.ataccama2.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;


@RestController
public class DataController {
    @Autowired
    private DataService dataService;

    @GetMapping("/schemas")
    public @ResponseBody Collection<String> getSchemas() {
        return dataService.getSchemas();
    }

    @GetMapping("/tables")
    public @ResponseBody Collection<String> getTables(@RequestParam String name) throws Exception {
        return dataService.getTables(name);
    }
}
