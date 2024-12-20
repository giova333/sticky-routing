package io.github.giova333.gateway.userprofile;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang.StringUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class UserProfileRoutingFilter implements GatewayFilter, Ordered {

    UserProfileHostResolver userProfileHostResolver;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String userId = extractUserId(request);

        if (isEmpty(userId)) {
            return chain.filter(exchange);
        }

        var host = userProfileHostResolver.resolveHost(userId);

        if (isEmpty(host)) {
            return Mono.error(new RuntimeException("Unable to find host for userId: " + userId));
        }

        var newExchange = mutateExchange(exchange, host, request);

        log.info("Routing request for user: [{}] to host: [{}]", userId, host);

        return chain.filter(newExchange);
    }

    private ServerWebExchange mutateExchange(ServerWebExchange exchange, String host, ServerHttpRequest request) {
        URI targetUri = URI.create("http://" + host + request.getURI().getPath());
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, targetUri);
        return exchange;
    }

    private String extractUserId(ServerHttpRequest request) {
        var pathParts = request.getPath().pathWithinApplication().value().split("/");
        return pathParts[pathParts.length - 1];
    }

    @Override
    public int getOrder() {
        return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER + 1;
    }
}
