package io.github.giova333.gateway.userprofile;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.internals.BuiltInPartitioner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class Partitioner {

    @Value("${user-profile-service.partitions}")
    private int partitionsCount;

    public int partition(String key) {
        return BuiltInPartitioner.partitionForKey(key.getBytes(StandardCharsets.UTF_8), partitionsCount);
    }
}
