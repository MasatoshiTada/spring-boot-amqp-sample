package com.example.amqp.producer.service;

import com.example.amqp.producer.domain.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AmqpService {

    private final Logger logger = LoggerFactory.getLogger(AmqpService.class);

    private final RabbitTemplate rabbitTemplate;

    public AmqpService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 1000L)
    public void send() {
        MyMessage myMessage = new MyMessage("Hello!!");
        logger.info("SENDING : " + myMessage);
        rabbitTemplate.convertAndSend("myQueue", myMessage);
    }

}
