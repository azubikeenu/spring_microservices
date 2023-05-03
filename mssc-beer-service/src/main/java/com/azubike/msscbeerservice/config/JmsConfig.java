package com.azubike.msscbeerservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {
    public static final String BREWING_REQUEST_QUEUE = "brewing_request";
    public static final String NEW_INVENTORY_QUEUE = "new-inventory";

    public static final String VALIDATE_ORDER_QUEUE = "validate-order";

    public static  final String VALIDATE_ORDER_RESPONSE = "validate-order-response";

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper){
        // this creates a bidirectional mapping between our POJO objects to JSON
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
         converter.setTargetType(MessageType.TEXT);
         converter.setTypeIdPropertyName("_type");
         converter.setObjectMapper(objectMapper);
        return converter;
    }

}