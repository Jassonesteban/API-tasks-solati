package com.task.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.task.api")
@EnableJpaRepositories(basePackages = "com.task.api.repository")
public class ApItaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApItaskApplication.class, args);
	}

}
