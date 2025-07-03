package com.nocodebi.service;

import com.nocodebi.service.cookieManager.CookieStoreManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceApplication {

	public static CookieStoreManager manager = new CookieStoreManager();

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

}
