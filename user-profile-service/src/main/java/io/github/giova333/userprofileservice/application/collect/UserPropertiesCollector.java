package io.github.giova333.userprofileservice.application.collect;

import io.github.giova333.userprofileservice.domain.UserProperty;

public interface UserPropertiesCollector {

    void collect(UserProperty property);
}
