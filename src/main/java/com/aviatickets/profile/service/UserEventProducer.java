package com.aviatickets.profile.service;

import com.aviatickets.profile.model.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public boolean sendEvent(UserEvent userEvent) {
        String eventId = UUID.randomUUID().toString();
        String sendedAt = ZonedDateTime.now().toString();

        try {
            String eventJson = objectMapper.writeValueAsString(userEvent);

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("profileUserSync", eventJson);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send event to Kafka", ex);
                } else {
                    log.info("Event sent to Kafka topic: {}", eventJson);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error serializing event", e);
            return false;
        }
        return true;
    }
}

