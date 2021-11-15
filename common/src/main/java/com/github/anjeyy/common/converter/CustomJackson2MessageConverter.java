package com.github.anjeyy.common.converter;

import com.github.anjeyy.common.annotation.ExcludeFromGeneratedJacocoReport;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@ExcludeFromGeneratedJacocoReport
public class CustomJackson2MessageConverter {

    private CustomJackson2MessageConverter() {
        throw new UnsupportedOperationException("No instance allowed");
    }

    public static MappingJackson2MessageConverter mappingJackson2MessageConverter() {
        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        mappingJackson2MessageConverter.getObjectMapper().findAndRegisterModules(); //crucial for Java 8 LocalDateTime
        return mappingJackson2MessageConverter;
    }
}
