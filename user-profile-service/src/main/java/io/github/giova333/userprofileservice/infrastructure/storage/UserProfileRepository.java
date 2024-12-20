package io.github.giova333.userprofileservice.infrastructure.storage;

import io.github.giova333.userprofileservice.domain.UserProfile;
import io.github.giova333.userprofileservice.domain.UserProperty;

public interface UserProfileRepository {

    UserProfile getUserProfile(String userId);

    void collect(UserProperty property);
}
