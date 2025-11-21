package com.nhi.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.nhi.libary", "com.nhi.admin"})
@EnableJpaRepositories(value = "com.nhi.libary.repository")
@EntityScan(value =   "com.nhi.libary.model")
public class SpringbootMusicWebAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMusicWebAdminApplication.class, args);
	}

}
