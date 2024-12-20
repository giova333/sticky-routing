package io.github.giova333.userprofileservice.infrastructure.web;

import io.github.giova333.userprofileservice.application.core.UserProfileProvider;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class UserProfileController {

    UserProfileProvider userProfileProvider;

    @GetMapping("/{userId}")
    public UserProfileResponse getUserProfile(@PathVariable String userId) {
        var userProfile = userProfileProvider.getUserProfile(userId);

        return new UserProfileResponse(userProfile.userId(), userProfile.properties());
    }
}
