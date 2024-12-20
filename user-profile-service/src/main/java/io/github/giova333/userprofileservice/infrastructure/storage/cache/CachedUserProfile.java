package io.github.giova333.userprofileservice.infrastructure.storage.cache;

import io.github.giova333.userprofileservice.domain.UserProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CachedUserProfile {
    String userId;
    Map<String, Object> properties;

    public void addProperty(UserProperty property) {
        properties.put(property.name(), property.value());
    }
}
