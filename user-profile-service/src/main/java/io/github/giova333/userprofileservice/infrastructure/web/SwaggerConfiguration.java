package io.github.giova333.userprofileservice.infrastructure.web;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    GroupedOpenApi segmentationPropertiesApi() {
        return GroupedOpenApi.builder()
                .group("user-profile-service")
                .packagesToScan("io.github.giova333.userprofileservice.infrastructure.web")
                .build();
    }

}
