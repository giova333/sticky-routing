package io.github.giova333.userprofileservice.infrastructure.kafka.collect.configuration;

import io.github.giova333.userprofileservice.infrastructure.kafka.collect.RoutePropertyEvent;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class RouterKafkaTemplateConfiguration {

    @Bean
    public ProducerFactory<String, RoutePropertyEvent> routerProducerFactory(
            KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, RoutePropertyEvent> routerKafkaTemplate(
            ProducerFactory<String, RoutePropertyEvent> routerProducerFactory) {
        return new KafkaTemplate<>(routerProducerFactory);
    }
}
