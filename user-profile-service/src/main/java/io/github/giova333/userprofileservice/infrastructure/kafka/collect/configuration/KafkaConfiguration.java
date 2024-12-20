package io.github.giova333.userprofileservice.infrastructure.kafka.collect.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class KafkaConfiguration {

    @Bean
    public static UserPropertyEventConsumerFactory userPropertyEventConsumerFactory(ApplicationContext applicationContext,
                                                                                    Environment environment) {
        return new UserPropertyEventConsumerFactory(applicationContext, environment);
    }
}
