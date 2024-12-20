package io.github.giova333.userprofileservice.infrastructure.web;

import lombok.Builder;

import java.util.Map;

@Builder
public record UserProfileResponse(
        String userId,
        Map<String, Object> properties) {
}
