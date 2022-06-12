package com.tweetapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TweetListener {

	Logger log=LoggerFactory.getLogger(TweetListener.class);
	@KafkaListener(topics="tweets",
			groupId="groupId")
	void listener(String data) {
		
		log.info("Listener Called : "+data);
	}
	
}
