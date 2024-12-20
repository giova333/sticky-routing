package io.github.giova333.userprofileservice.application.collect;

import io.github.giova333.userprofileservice.domain.UserProperty;
import io.github.giova333.userprofileservice.infrastructure.kafka.collect.RoutePropertyEvent;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RouterUserPropertiesCollector implements UserPropertiesCollector {

    String topicName;

    KafkaTemplate<String, RoutePropertyEvent> kafkaTemplate;

    public RouterUserPropertiesCollector(
            @Value("${router.topic.name}") String topicName,
            KafkaTemplate<String, RoutePropertyEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @Override
    public void collect(UserProperty property) {
        var event = RoutePropertyEvent.builder()
                .userId(property.userId())
                .name(property.name())
                .value(property.value())
                .build();

        kafkaTemplate.send(topicName, property.userId(), event);

        log.debug("User property event sent to router: {}", event);
    }
}
