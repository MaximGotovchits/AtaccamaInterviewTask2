package com.project.ataccama2.controller;

import com.project.ataccama2.model.Schema;
import com.project.ataccama2.service.DataService;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

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
}
