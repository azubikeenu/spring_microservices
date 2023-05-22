package guru.sfg.beer.order.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {
    public static final String VALIDATE_ORDER_QUEUE = "validate-order";
    public static final String ALLOCATE_ORDER_QUEUE = "allocate-order";

    public static  final String VALIDATE_ORDER_RESPONSE = "validate-order-response";

    public static final String ALLOCATE_ORDER_RESPONSE_QUEUE  = "allocate-order-response";
    @Bean
    public MappingJackson2MessageConverter  messageConverter(ObjectMapper objectMapper){
        // this creates a bidirectional mapping between our POJO objects to JSON
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
         converter.setTargetType(MessageType.TEXT);
         converter.setTypeIdPropertyName("_type");
         converter.setObjectMapper(objectMapper);
        return converter;
    }


}