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
