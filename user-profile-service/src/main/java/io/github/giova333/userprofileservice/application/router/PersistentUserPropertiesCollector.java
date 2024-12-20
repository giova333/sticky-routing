package io.github.giova333.userprofileservice.application.router;

import io.github.giova333.userprofileservice.application.collect.UserPropertiesCollector;
import io.github.giova333.userprofileservice.domain.UserProperty;
import io.github.giova333.userprofileservice.infrastructure.storage.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PersistentUserPropertiesCollector implements UserPropertiesCollector {

    UserProfileRepository cacheUserProfileRepository;

    @Override
    public void collect(UserProperty property) {
        cacheUserProfileRepository.collect(property);

        log.info("User property collected: [{}]", property);
    }
}
