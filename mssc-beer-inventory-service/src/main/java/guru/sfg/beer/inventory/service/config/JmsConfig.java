package guru.sfg.beer.inventory.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {
    @Bean
    public MessageConverter messageConverter(){
        // this creates a bidirectional mapping between our POJO objects to JSON
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
         converter.setTargetType(MessageType.TEXT);
         converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
