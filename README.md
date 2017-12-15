Spring Boot AMQP Sample
=======================

# 演習の概要
- RabbitMQにJSONを送信しつづけるプロデューサーと、そのJSONをRabbitMQから受信するコンシューマーを作成します。
- 目標時間：15分


# 1. プロデューサーの作成

## 手順1-1 プロジェクトの作成
Spring Initializrでプロジェクトを作成します。 `spring-boot-starter-amqp` を依存性に含めます。

pom.xmlに、下記の依存性を追記してください。

```xml
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
```


## 手順1-2 メッセージクラスの作成

```java
package com.example.amqp.producer.domain;

import java.time.LocalDateTime;

public class MyMessage {

    private String message;

    private LocalDateTime postedAt;

    public MyMessage(String message) {
        this.message = message;
        this.postedAt = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    @Override
    public String toString() {
        return "MyMessage{" +
                "message='" + message + '\'' +
                ", postedAt=" + postedAt +
                '}';
    }
}
```

## 手順1-3 Configurationクラスの作成

```java
package com.example.amqp.producer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class RabbitConfiguration {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        // Date and Time APIに対応したObjectMapperを作成する
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .dateFormat(new StdDateFormat())
                .timeZone("Asia/Tokyo")
                .build();
        Jackson2JsonMessageConverter jackson2JsonMessageConverter
                = new Jackson2JsonMessageConverter(objectMapper);
        return jackson2JsonMessageConverter;
    }
    
    @Bean
    public Queue myQueue() {
    	return new Queue("myQueue");
    }

}
```

## 手順1-4 メッセージ送信クラスの作成

```java
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
```

## 手順1-5 mainクラスの作成

```java
package com.example.amqp.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // このアノテーションを付加する
@SpringBootApplication
public class AmqpProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmqpProducerApplication.class, args);
    }

}
```

## 1-6 起動

このプロジェクトを起動します。1秒に1回、RabbitMQにメッセージが送信されます。

```bash
...
2017-10-26 14:48:38.494  INFO 14014 --- [pool-3-thread-1] c.e.amqp.producer.service.AmqpService    : SENDING : MyMessage{message='Hello!!', postedAt=2017-10-26T14:48:38.494}
2017-10-26 14:48:39.496  INFO 14014 --- [pool-3-thread-1] c.e.amqp.producer.service.AmqpService    : SENDING : MyMessage{message='Hello!!', postedAt=2017-10-26T14:48:39.496}
2017-10-26 14:48:40.499  INFO 14014 --- [pool-3-thread-1] c.e.amqp.producer.service.AmqpService    : SENDING : MyMessage{message='Hello!!', postedAt=2017-10-26T14:48:40.499}
2017-10-26 14:48:41.503  INFO 14014 --- [pool-3-thread-1] c.e.amqp.producer.service.AmqpService    : SENDING : MyMessage{message='Hello!!', postedAt=2017-10-26T14:48:41.503}
2017-10-26 14:48:42.509  INFO 14014 --- [pool-3-thread-1] c.e.amqp.producer.service.AmqpService    : SENDING : MyMessage{message='Hello!!', postedAt=2017-10-26T14:48:42.509}
...
```


<!------------------------------------------------------------------->

# 2. コンシューマーの作成
  
## 手順2-1 プロジェクトの作成

Spring Initializrでプロジェクトを作成します。 `spring-boot-starter-amqp` を依存性に含めます。

pom.xmlに、下記の依存性を追記してください。

```xml
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
```

## 手順2-2 メッセージクラスの作成

```java
package com.example.amqp.consumer.domain;

import java.time.LocalDateTime;

public class MyMessage {

    private String message;

    private LocalDateTime postedAt;

    public MyMessage() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    @Override
    public String toString() {
        return "MyMessage{" +
                "message='" + message + '\'' +
                ", postedAt=" + postedAt +
                '}';
    }
}
```

## 手順2-3 Configurationクラスの作成

```java
package com.example.amqp.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class RabbitConfiguration {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        // Date and Time APIに対応したObjectMapperを作成する
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .dateFormat(new StdDateFormat())
                .timeZone("Asia/Tokyo")
                .build();
        Jackson2JsonMessageConverter jackson2JsonMessageConverter
                = new Jackson2JsonMessageConverter(objectMapper);
        return jackson2JsonMessageConverter;
    }

}
```

## 手順2-4 メッセージ受信クラスの作成

```java
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
```

> mainクラスは変更する必要はありません。

## 2-5 起動

このプロジェクトを起動します。

最初は、RabbitMQに蓄積されていたメッセージが一気に受信されます。その後は、プロデューサーが送信されたメッセージが1秒に1回受信されます。

```bash
...
2017-10-26 14:54:05.244  INFO 15639 --- [cTaskExecutor-1] c.e.amqp.consumer.service.AmqpService    : RECEIVED : MyMessage{message='Hello!!', postedAt=2017-10-26T14:48:38.494}
2017-10-26 14:54:05.245  INFO 15639 --- [cTaskExecutor-1] c.e.amqp.consumer.service.AmqpService    : RECEIVED : MyMessage{message='Hello!!', postedAt=2017-10-26T14:48:39.496}
2017-10-26 14:54:05.246  INFO 15639 --- [cTaskExecutor-1] c.e.amqp.consumer.service.AmqpService    : RECEIVED : MyMessage{message='Hello!!', postedAt=2017-10-26T14:48:40.499}
2017-10-26 14:54:05.247  INFO 15639 --- [cTaskExecutor-1] c.e.amqp.consumer.service.AmqpService    : RECEIVED : MyMessage{message='Hello!!', postedAt=2017-10-26T14:48:41.503}
2017-10-26 14:54:05.247  INFO 15639 --- [cTaskExecutor-1] c.e.amqp.consumer.service.AmqpService    : RECEIVED : MyMessage{message='Hello!!', postedAt=2017-10-26T14:48:42.509}
...
```

この演習は以上です。
