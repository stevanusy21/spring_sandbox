package com.springboot.sandbox.gateway.config;

import static org.springframework.cloud.gateway.server.mvc.filter.Bucket4jFilterFunctions.rateLimit;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.AsyncProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;

@Configuration
public class RateLimiterConfig {

    @Bean
    public AsyncProxyManager<String> lettuceProxyManager() {
        RedisClient redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        var connection = redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
        return Bucket4jLettuce.casBasedBuilder(connection)
            .expirationAfterWrite(
                ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1))
            )
            .build()
            .asAsync();
    }

    // Rute Auth API — dengan Rate Limiter
    @Bean
    public RouterFunction<ServerResponse> authRoute() {
        return route("auth-api-route")
            .route(req -> req.uri().getPath().startsWith("/api/auth"), http())
            .filter(lb("auth-service"))  // lb() WAJIB, bukan .before(uri("lb://..."))
            .filter(rateLimit(config -> config
                .setCapacity(5)
                .setPeriod(Duration.ofSeconds(1))
                .setKeyResolver(req -> req.remoteAddress()
                    .map(addr -> addr.getAddress().getHostAddress())
                    .orElse("anonymous"))
            ))
            .build();
    }

    // Rute Swagger Auth Docs — tanpa Rate Limiter
    @Bean
    public RouterFunction<ServerResponse> authDocsRoute() {
        return route("auth-docs-route")
            .route(req -> req.uri().getPath().startsWith("/v1/api-docs/auth-service"), http())
            .filter(lb("auth-service"))  // lb() juga wajib di sini
            .build();
    }
}
