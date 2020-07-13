package com.example.awsboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @EnableJpaAuditing
@SpringBootApplication
public class AwsboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsboardApplication.class, args);
	}

}
