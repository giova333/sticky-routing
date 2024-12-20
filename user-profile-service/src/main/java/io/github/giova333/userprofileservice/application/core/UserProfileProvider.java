package io.github.giova333.userprofileservice.application.core;

import io.github.giova333.userprofileservice.domain.UserProfile;

public interface UserProfileProvider {

    UserProfile getUserProfile(String userId);
}
