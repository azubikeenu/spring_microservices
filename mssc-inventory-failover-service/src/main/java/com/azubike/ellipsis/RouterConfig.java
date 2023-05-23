package com.azubike.ellipsis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> inventoryRoute(InventoryHandler inventoryHandler) {
        return route(GET("/api/v1/beer/{beerId}/inventory")
                        .and(accept(MediaType.APPLICATION_JSON)),
                inventoryHandler::listInventory);
    }
}
