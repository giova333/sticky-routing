package io.github.giova333.userprofileservice.infrastructure.storage.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "cache.configuration")
public class CacheConfigurationProperties {

    int maxNumberOfItems;
    int numberOfPartitions;
    Duration expireAfterWrite;
}
