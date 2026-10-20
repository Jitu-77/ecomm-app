package com.ecommApp.ecomm_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class EcommAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommAppApplication.class, args);
	}

}
