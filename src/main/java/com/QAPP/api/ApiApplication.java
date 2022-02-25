package com.QAPP.api;

import com.QAPP.api.utility.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
@EnableConfigurationProperties({
		ConfigProperties.class
})
public class ApiApplication {

	@Autowired
	ConfigProperties configProperties;

	public static void main(String[] args) {

		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("*")
						.allowedOrigins("http://localhost:3000");
			}
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}




	@Bean
	public JavaMailSender getJavaMailSender()
	{
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(configProperties.getHost());
		mailSender.setPort(Integer.parseInt(configProperties.getPort()));
		mailSender.setUsername(configProperties.getUsername());
		mailSender.setPassword(configProperties.getPassword());
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");
		return mailSender;
	}

}
