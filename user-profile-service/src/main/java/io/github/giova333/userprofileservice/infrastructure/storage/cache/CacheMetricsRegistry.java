package io.github.giova333.userprofileservice.infrastructure.storage.cache;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CacheMetricsRegistry {

    MeterRegistry meterRegistry;
    CaffeinePartitionedContainer caffeinePartitionedContainer;

    @EventListener
    public void onStartUp(ApplicationReadyEvent event) {
        Gauge.builder("cache.size", caffeinePartitionedContainer::size)
                .register(meterRegistry);
    }
}
