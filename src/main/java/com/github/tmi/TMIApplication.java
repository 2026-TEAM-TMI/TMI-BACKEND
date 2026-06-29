package com.github.tmi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TMIApplication {
	public static void main(String[] args) {
		SpringApplication.run(TMIApplication.class, args);
	}

}
