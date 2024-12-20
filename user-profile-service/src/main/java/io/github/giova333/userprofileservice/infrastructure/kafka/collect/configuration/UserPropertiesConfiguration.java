package io.github.giova333.userprofileservice.infrastructure.kafka.collect.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@ConfigurationProperties
public class UserPropertiesConfiguration {

    List<UserPropertyDefinition> userProperties;

    public List<UserPropertyDefinition> getByTopicName(String topic) {
        return userProperties.stream()
                .filter(userPropertyDefinition -> userPropertyDefinition.getTopic().equals(topic))
                .toList();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    public static class UserPropertyDefinition {
        String name;
        String topic;
        String path;

        public String getPath() {
            return path == null ? name : path;
        }
    }
}
