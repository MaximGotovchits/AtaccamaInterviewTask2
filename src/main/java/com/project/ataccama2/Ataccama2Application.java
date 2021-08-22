package com.project.ataccama2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Ataccama2Application {

	public static void main(String[] args) {
		SpringApplication.run(Ataccama2Application.class, args);
	}

}
