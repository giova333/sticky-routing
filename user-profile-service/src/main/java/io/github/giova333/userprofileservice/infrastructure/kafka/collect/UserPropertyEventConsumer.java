package io.github.giova333.userprofileservice.infrastructure.kafka.collect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.giova333.userprofileservice.application.collect.UserPropertiesCollector;
import io.github.giova333.userprofileservice.domain.UserProperty;
import io.github.giova333.userprofileservice.infrastructure.kafka.collect.configuration.KafkaTopicsConfiguration;
import io.github.giova333.userprofileservice.infrastructure.kafka.collect.configuration.UserPropertiesConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = PRIVATE)
@RequiredArgsConstructor
public class UserPropertyEventConsumer implements MessageListener<String, Bytes> {

    ObjectMapper objectMapper;
    SpelValueExtractor spelValueExtractor;
    KafkaTopicsConfiguration kafkaTopicsConfiguration;
    UserPropertiesConfiguration userPropertiesConfiguration;
    UserPropertiesCollector routerUserPropertiesCollector;

    @Override
    public void onMessage(ConsumerRecord<String, Bytes> message) {
        var topic = message.topic();
        var topicConfiguration = kafkaTopicsConfiguration.getKafkaTopicConfiguration(topic);

        var event = parseEventPayload(message);
        var userId = spelValueExtractor.extractValue(event, topicConfiguration.getUserIdSource(), String.class);

        userPropertiesConfiguration.getByTopicName(topic)
                .forEach(property -> {
                    var propertyValue = spelValueExtractor.extractValue(event, property.getPath());
                    var userProperty = UserProperty.builder()
                            .userId(userId)
                            .name(property.getName())
                            .value(propertyValue)
                            .build();

                    routerUserPropertiesCollector.collect(userProperty);
                });

    }

    private Map<String, Object> parseEventPayload(ConsumerRecord<String, Bytes> message) {
        try {
            return objectMapper.readValue(message.value().get(), new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error("Failed to parse message: [{}]", message);
            throw new RuntimeException("Failed to parse message", e);
        }
    }
}
