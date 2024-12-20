package io.github.giova333.userprofileservice.domain;

import lombok.Builder;

@Builder
public record UserProperty(
        String userId,
        String name,
        Object value) {
}
