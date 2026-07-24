package com.homeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HomeserverApplication {
	public static void main(String[] args) {
		SpringApplication.run(HomeserverApplication.class, args);
	}

}
