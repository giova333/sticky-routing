package io.github.giova333.gateway.userprofile;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class UserProfileRoutingGatewayFilterFactory extends AbstractGatewayFilterFactory<UserProfileRoutingGatewayFilterFactory.Config> {

    private final UserProfileRoutingFilter filter;

    public UserProfileRoutingGatewayFilterFactory(UserProfileRoutingFilter filter) {
        super(Config.class);
        this.filter = filter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return filter;
    }

    public record Config() {
    }

    @Override
    public String name() {
        return "UserProfileRoutingFilter";
    }
}
