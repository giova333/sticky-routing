package io.github.giova333.userprofileservice.infrastructure.kafka.collect.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Properties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@ConfigurationProperties
public class KafkaTopicsConfiguration {

    List<KafkaTopicConfiguration> kafkaTopics;

    public KafkaTopicConfiguration getKafkaTopicConfiguration(String topic) {
        return kafkaTopics.stream()
                .filter(kafkaTopicConfiguration -> kafkaTopicConfiguration.getTopic().equals(topic))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No configuration found for topic: " + topic));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    public static class KafkaTopicConfiguration {

        static String DEFAULT_USER_ID_SOURCE = "userId";

        String topic;
        String userIdSource = DEFAULT_USER_ID_SOURCE;
        int concurrency = 1;
        Properties properties = new Properties();
    }

}
