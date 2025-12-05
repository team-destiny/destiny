package com.destiny.couponservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.destiny")
public class CouponServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouponServiceApplication.class, args);
	}

}
