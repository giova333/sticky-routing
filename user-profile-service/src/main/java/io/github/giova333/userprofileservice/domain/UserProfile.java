package io.github.giova333.userprofileservice.domain;

import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static java.util.Collections.emptyMap;

@Builder
public record UserProfile(
        String userId,
        Map<String, Object> properties) {

    public static UserProfile empty(String userId) {
        return new UserProfile(userId, emptyMap());
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(properties);
    }
}
