package com.example.amqp.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AmqpProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmqpProducerApplication.class, args);
    }

}
