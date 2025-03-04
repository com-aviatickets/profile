package com.aviatickets.profile.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;



@Component
public class UserEventConsumer {
    @KafkaListener(topics = "profileUserSync", groupId = "your-group-id")
    public void listen(String message) {
        System.out.println("Received Message: " + message);
    }
}
