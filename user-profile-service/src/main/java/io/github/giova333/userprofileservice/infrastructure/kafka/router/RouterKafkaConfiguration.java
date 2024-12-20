package io.github.giova333.userprofileservice.infrastructure.kafka.router;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.ApplicationInfoManager;
import io.github.giova333.userprofileservice.application.collect.UserPropertiesCollector;
import io.github.giova333.userprofileservice.infrastructure.kafka.collect.RoutePropertyEvent;
import io.github.giova333.userprofileservice.infrastructure.storage.cache.CaffeinePartitionedContainer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
public class RouterKafkaConfiguration {

    @Bean
    public RoutePropertyEventListener routePropertyEventListener(UserPropertiesCollector persistentUserPropertiesCollector) {
        return new RoutePropertyEventListener(persistentUserPropertiesCollector);
    }

    @Bean
    public RouterKafkaRebalanceCacheInvalidator routerKafkaRebalanceCacheInvalidator(CaffeinePartitionedContainer cache) {
        return new RouterKafkaRebalanceCacheInvalidator(cache);
    }

    @Bean
    public RouterKafkaRebalanceListener routerCacheInvalidationKafkaRebalanceListener(RouterKafkaRebalanceCacheInvalidator invalidator,
                                                                                      ApplicationInfoManager applicationInfoManager,
                                                                                      ObjectMapper objectMapper) {
        return new RouterKafkaRebalanceListener(invalidator, applicationInfoManager, objectMapper);
    }

    @Bean
    public ConsumerFactory<byte[], RoutePropertyEvent> routerKafkaConsumerFactory(
            KafkaProperties kafkaProperties,
            @Value("${spring.application.name}") String consumerGroupId) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                consumerGroupId);

        Deserializer<byte[]> keyDeserializer = new ByteArrayDeserializer();
        Deserializer<RoutePropertyEvent> valueDeserializer = new ErrorHandlingDeserializer<>(
                new JsonDeserializer<>(RoutePropertyEvent.class));
        return new DefaultKafkaConsumerFactory<>(props, keyDeserializer, valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<byte[], RoutePropertyEvent> routerKafkaListenerContainerFactory(
            ConsumerFactory<byte[], RoutePropertyEvent> routerKafkaConsumerFactory,
            @Value("${router.topic.concurrency}") int concurrency,
            RouterKafkaRebalanceListener rebalanceListener) {
        ConcurrentKafkaListenerContainerFactory<byte[], RoutePropertyEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(routerKafkaConsumerFactory);
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setConsumerRebalanceListener(rebalanceListener);
        factory.setCommonErrorHandler(new DefaultErrorHandler());
        return factory;
    }
}
