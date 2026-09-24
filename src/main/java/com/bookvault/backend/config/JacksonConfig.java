package com.bookvault.backend.config;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJacksonCustomizer() {
        return builder -> {
            StreamReadConstraints constraints = StreamReadConstraints.builder()
                    .maxStringLength(100_000_000) // 100 MB max string length for base64 / data URLs
                    .build();
            builder.postConfigurer(objectMapper -> 
                objectMapper.getFactory().setStreamReadConstraints(constraints)
            );
        };
    }
}
