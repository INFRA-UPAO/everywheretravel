package com.everywhere.backend.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
        
        // Conversor para convertir String a mayúsculas
        modelMapper.addConverter(context -> {
            String source = context.getSource();
            return source == null ? null : source.toUpperCase();
        }, String.class, String.class);
        
        return modelMapper;
    }
}