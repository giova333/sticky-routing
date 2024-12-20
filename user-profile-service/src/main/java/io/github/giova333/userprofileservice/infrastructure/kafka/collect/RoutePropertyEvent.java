package io.github.giova333.userprofileservice.infrastructure.kafka.collect;

import lombok.Builder;

@Builder
public record RoutePropertyEvent(
        String userId,
        String name,
        Object value) {
}
