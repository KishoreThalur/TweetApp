package com.tweetapp;

import java.util.Arrays;
import java.util.Collections;

import org.apache.kafka.common.message.AlterIsrResponseData.TopicData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class TweetApplication {

	public static void main(String[] args) {
		SpringApplication.run(TweetApplication.class, args);
	}

	 @Bean
	    public CorsFilter corsFilter() {
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        CorsConfiguration config = new CorsConfiguration();
	        config.setAllowCredentials(true);

	        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
	        config.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin","Content-Type", "Accept" ,"Authorization", "Origin , Accept","X-Requested-With","Access-Control-Request-Method" ,"Access-Control-Request-Headers"));
	        config.setExposedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin","Content-Type", "Accept" ,"Authorization", "Origin , Accept","Access-Control-Allow-Credentials"));
	        
//	        config.setAllowedOrigins(Collections.singletonList("*"));
	        //config.setAllowedHeaders(Arrays.asList("*"));
	        config.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
	        source.registerCorsConfiguration("/**", config);
	        return new CorsFilter(source);
	    }
	
	@Bean
	CommandLineRunner commandLineRunner(KafkaTemplate<String,String> kafkaTemplate){
		return args->{
			kafkaTemplate.send( "tweets", "TweetDAO");		
			};
	}
}
