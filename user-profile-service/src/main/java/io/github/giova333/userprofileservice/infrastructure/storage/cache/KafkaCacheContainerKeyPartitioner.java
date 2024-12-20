package io.github.giova333.userprofileservice.infrastructure.storage.cache;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.producer.internals.BuiltInPartitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaCacheContainerKeyPartitioner implements CacheContainerKeyPartitioner {

    @Value("${router.topic.partitions}")
    int topicPartitionsCount;

    @Override
    public int partition(String key) {
        return BuiltInPartitioner.partitionForKey(key.getBytes(StandardCharsets.UTF_8), topicPartitionsCount);
    }
}
