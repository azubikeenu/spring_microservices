package com.azubike.ellipsis;

import common.models.BeerInventoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class InventoryHandler {
    public Mono<ServerResponse> listInventory(ServerRequest request){
        log.debug("Calling failover service");
        final UUID id =UUID.fromString(request.pathVariable("beerId"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(Mono.just(Collections.singletonList(
                        BeerInventoryDto.builder()
                                .id(UUID.randomUUID())
                                .beerId(id)
                                .quantityOnHand(999)
                                .createdDate(OffsetDateTime.now())
                                .lastModifiedDate(OffsetDateTime.now())
                                .build())), List.class);
    }
}
