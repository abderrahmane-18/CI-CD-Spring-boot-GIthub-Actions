package com.example.spring_boot.ci.cd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {
@GetMapping ("/hello")
	public String print()
	{
		return  "Hello Wold";
	}
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
