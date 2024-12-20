/*
 * Copyrights 2021 Playtika Ltd., all rights reserved to Playtika Ltd.
 * privacy+e17bb14d-edc1-4d26-930d-486fcc1ab8fe@playtika.com
 */

package io.github.giova333.userprofileservice.infrastructure.kafka.router;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.ApplicationInfoManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class RouterKafkaRebalanceListener implements ConsumerAwareRebalanceListener {

    private final RouterKafkaRebalanceCacheInvalidator invalidator;
    private final ApplicationInfoManager applicationInfoManager;
    private final ObjectMapper objectMapper;

    @Override
    public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        log.info("Invalidating Router cache on partitions revoke: {}", partitions);

        final List<Integer> partitionIndexes = partitions.stream()
                .map(TopicPartition::partition)
                .toList();

        invalidator.invalidate(partitionIndexes);

        updateDiscoveryMetadata(consumer);
    }

    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        updateDiscoveryMetadata(consumer);
    }

    @SneakyThrows
    private void updateDiscoveryMetadata(Consumer<?, ?> consumer) {
        var assignedPartitions = consumer.assignment()
                .stream()
                .map(TopicPartition::partition)
                .toList();

        var memberId = consumer.groupMetadata().memberId();
        var key = "partitions-" + memberId;

        var partitions = objectMapper.writeValueAsString(assignedPartitions);

        applicationInfoManager.registerAppMetadata(Map.of(key, partitions));

        log.info("Updated discovery metadata on partitions update: {}", assignedPartitions);
    }
}
