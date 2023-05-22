package com.azubike.ellipsis.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

//@Profile("google")
//@Configuration
public class GoogleConfig {
    @Bean
    RouteLocator googleRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes().
                route(r ->
                r.path("/google-search").uri("https://google.com")).build();
    }
}
