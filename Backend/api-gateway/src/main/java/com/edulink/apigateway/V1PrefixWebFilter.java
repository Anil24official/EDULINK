package com.edulink.apigateway;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Spec §8 — API versioning. Strips the {@code /v1/} prefix from incoming requests
 * BEFORE Spring Cloud Gateway evaluates route predicates, so existing routes
 * keyed on {@code /auth/**}, {@code /student/**}, etc. continue to match.
 * <p>
 * Both {@code /auth/login} and {@code /v1/auth/login} reach the same backend, which
 * means clients can adopt {@code /v1} progressively without breaking integrations.
 * Runs at HIGHEST_PRECEDENCE so it executes before any downstream filter.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class V1PrefixWebFilter implements WebFilter {

    private static final String PREFIX = "/v1";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getRawPath();
        if (path != null && (path.equals(PREFIX) || path.startsWith(PREFIX + "/"))) {
            String stripped = path.length() == PREFIX.length() ? "/" : path.substring(PREFIX.length());
            ServerHttpRequest mutated = request.mutate().path(stripped).build();
            return chain.filter(exchange.mutate().request(mutated).build());
        }
        return chain.filter(exchange);
    }
}
