/*
 * Copyrights 2023 Playtika Ltd., all rights reserved to Playtika Ltd.
 * privacy+e17bb14d-edc1-4d26-930d-486fcc1ab8fe@playtika.com
 */

package io.github.giova333.userprofileservice.infrastructure.kafka.router;

import io.github.giova333.userprofileservice.infrastructure.storage.cache.CaffeinePartitionedContainer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RouterKafkaRebalanceCacheInvalidator {

    CaffeinePartitionedContainer cache;

    void invalidate(List<Integer> kafkaPartitions) {
        cache.invalidateAll(kafkaPartitions);
    }

}
