package com.nhi.artist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {"com.nhi.libary", "com.nhi.artist"})
@EnableJpaRepositories(value = "com.nhi.libary.repository")
@EntityScan(value =   "com.nhi.libary.model")
public class SpringbootMusicWebArtistApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMusicWebArtistApplication.class, args);
	}

}
