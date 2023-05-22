package com.azubike.ellipsis.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("google")
@Configuration
public class GoogleConfig {
    @Bean
    RouteLocator googleRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        System.out.println("############### I RAN###################");
        return routeLocatorBuilder
                .routes().
                route(r ->
                r.path("/google-search").uri("https://google.com")).build();
    }
}
