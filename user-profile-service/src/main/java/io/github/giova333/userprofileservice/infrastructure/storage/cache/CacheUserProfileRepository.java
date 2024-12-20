package io.github.giova333.userprofileservice.infrastructure.storage.cache;

import io.github.giova333.userprofileservice.domain.UserProfile;
import io.github.giova333.userprofileservice.domain.UserProperty;
import io.github.giova333.userprofileservice.infrastructure.storage.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CacheUserProfileRepository implements UserProfileRepository {

    UserProfileRepository aerospikeUserProfileRepository;
    CaffeinePartitionedContainer caffeinePartitionedContainer;

    @Override
    public UserProfile getUserProfile(String userId) {
        return caffeinePartitionedContainer.get(userId)
                .map(this::toUserProfile)
                .orElseGet(() -> {
                    var userProfile = aerospikeUserProfileRepository.getUserProfile(userId);
                    if (!userProfile.isEmpty()) {
                        caffeinePartitionedContainer.put(toCachedUserProfile(userProfile));
                    }
                    return userProfile;
                });
    }

    @Override
    public void collect(UserProperty property) {
        aerospikeUserProfileRepository.collect(property);

        caffeinePartitionedContainer.actualize(property);
    }

    private CachedUserProfile toCachedUserProfile(UserProfile userProfile) {
        return CachedUserProfile.builder()
                .userId(userProfile.userId())
                .properties(userProfile.properties())
                .build();
    }

    private UserProfile toUserProfile(CachedUserProfile cachedUserProfile) {
        return UserProfile.builder()
                .userId(cachedUserProfile.getUserId())
                .properties(cachedUserProfile.getProperties())
                .build();
    }
}
