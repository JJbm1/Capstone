package com.example.chatbotserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EntityScan(basePackages = "com.example.chatbotserver")
@EnableJpaRepositories(basePackages = "com.example.chatbotserver")
public class ChatbotserverApplication {

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("✅ MySQL 드라이버 로드 성공!");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("❌ MySQL 드라이버 로딩 실패!", e);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(ChatbotserverApplication.class, args);
	}
}
