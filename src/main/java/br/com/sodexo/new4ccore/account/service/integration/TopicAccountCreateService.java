package br.com.sodexo.new4ccore.account.service.integration;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.sodexo.new4ccore.account.dto.CreateAccountTopicDTO;
import br.com.sodexo.new4ccore.account.interceptor.LoggerInterceptor;
import br.com.sodexo.new4ccore.account.util.LocalDateAdapter;
import br.com.sodexo.new4ccore.account.util.LocalDateTimeAdapter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TopicAccountCreateService {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${spring.jms.servicebus.topic-account-create}")
	private String topicName;

	private static Gson gson = new GsonBuilder()
	        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
	        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .disableHtmlEscaping()
	        .create();
	
	/**
	 * Send transaction information to topic
	 * @param transaction
	 */
	public void send(CreateAccountTopicDTO account) {
		jmsTemplate.convertAndSend(topicName, gson.toJson(account), jmsMessage -> {
			jmsMessage.setStringProperty("MessageID", topicName);
			jmsMessage.setStringProperty("ContentType", "application/json");
			jmsMessage.setStringProperty("RequestUUID", LoggerInterceptor.getUuid());
			jmsMessage.setJMSMessageID(account.getRequestId());
			return jmsMessage;
		});

		log.info("Account Message sent to {} - {}", topicName, account.getRequestId());
	}

}
