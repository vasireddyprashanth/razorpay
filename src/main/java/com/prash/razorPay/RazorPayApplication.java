package com.prash.razorPay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditingAwareImpl")
@EnableScheduling
public class RazorPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RazorPayApplication.class, args);
	}

}
