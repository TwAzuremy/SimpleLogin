package com.framework.simpleLogin;

import com.framework.simpleLogin.config.RedisProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(RedisProperties.class)
@ServletComponentScan
public class SimpleLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleLoginApplication.class, args);
	}

}
