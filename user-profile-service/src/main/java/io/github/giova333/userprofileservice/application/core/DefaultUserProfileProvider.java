package io.github.giova333.userprofileservice.application.core;

import io.github.giova333.userprofileservice.domain.UserProfile;
import io.github.giova333.userprofileservice.infrastructure.storage.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class DefaultUserProfileProvider implements UserProfileProvider {

    UserProfileRepository cacheUserProfileRepository;

    @Override
    public UserProfile getUserProfile(String userId) {
        var userProfile = cacheUserProfileRepository.getUserProfile(userId);
        log.info("Retrieved user profile: {}", userProfile);
        return userProfile;
    }
}
