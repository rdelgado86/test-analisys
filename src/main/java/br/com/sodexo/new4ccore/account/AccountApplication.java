package br.com.sodexo.new4ccore.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = "br.com.sodexo")
@Validated
public class AccountApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(AccountApplication.class, args);
	}

}
