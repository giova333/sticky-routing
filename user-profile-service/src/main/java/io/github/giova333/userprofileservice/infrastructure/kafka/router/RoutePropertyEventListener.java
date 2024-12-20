package io.github.giova333.userprofileservice.infrastructure.kafka.router;

import io.github.giova333.userprofileservice.application.collect.UserPropertiesCollector;
import io.github.giova333.userprofileservice.domain.UserProperty;
import io.github.giova333.userprofileservice.infrastructure.kafka.collect.RoutePropertyEvent;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoutePropertyEventListener {

    UserPropertiesCollector collector;

    @KafkaListener(topics = "${router.topic.name}", containerFactory = "routerKafkaListenerContainerFactory")
    public void listen(RoutePropertyEvent event) {
        var userProperty = new UserProperty(event.userId(), event.name(), event.value());

        collector.collect(userProperty);
    }

}
