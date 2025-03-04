package com.aviatickets.profile.service;

import com.aviatickets.profile.model.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;



@Service
public class UserProfileService {

    private static final String KAFKA_HOST = "localhost:9092";
    private static final String KAFKA_TOPIC = "profileUserSync";

    private KafkaProducer<String, String> producer;
    private ObjectMapper objectMapper;
    private UserEvent userEvent;

    public UserProfileService(){
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_HOST);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public boolean sendEvent(UserEvent userEvent) {
        String eventId = UUID.randomUUID().toString();
        String sendedAt = Instant.now().toString();

        try {
            String eventJson = objectMapper.writeValueAsString(userEvent); // Сериализация объекта UserEvent в JSON
            ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_TOPIC, eventJson);
            record.headers().add("eventId", eventId.getBytes());
            record.headers().add("sendedAt", sendedAt.getBytes());

            producer.send(record, (RecordMetadata metadata, Exception e) -> {
                if (e != null) {
                    e.printStackTrace();
                }
            });
            producer.flush();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
