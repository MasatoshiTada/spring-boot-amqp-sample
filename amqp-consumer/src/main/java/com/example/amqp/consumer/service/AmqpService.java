package com.example.amqp.consumer.service;

import com.example.amqp.consumer.domain.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class AmqpService {

    private final Logger logger = LoggerFactory.getLogger(AmqpService.class);

    @RabbitListener(queues = "myQueue")
    public void receive(MyMessage myMessage) {
        logger.info("RECEIVED : " + myMessage.toString());
    }
}
