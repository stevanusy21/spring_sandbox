package com.springboot.sandbox.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.springboot.sandbox.common.config.GlobalExceptionHandler;

@SpringBootApplication(
	scanBasePackages = {
		"com.springboot.sandbox.auth",
		"com.springboot.sandbox.common.util"
	},
	exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
	}
)
@EnableDiscoveryClient 
@Import(GlobalExceptionHandler.class)
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
