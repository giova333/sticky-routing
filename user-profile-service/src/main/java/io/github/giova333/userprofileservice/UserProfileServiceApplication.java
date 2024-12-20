package io.github.giova333.userprofileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class UserProfileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserProfileServiceApplication.class, args);
    }

}
