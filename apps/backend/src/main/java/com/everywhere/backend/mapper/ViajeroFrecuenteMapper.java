package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ViajeroFrecuenteRequestDto;
import com.everywhere.backend.model.dto.ViajeroFrecuenteResponseDto;
import com.everywhere.backend.model.entity.ViajeroFrecuente;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViajeroFrecuenteMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMappings() {
        modelMapper.typeMap(ViajeroFrecuenteRequestDto.class, ViajeroFrecuente.class).addMappings(mapper -> {
            mapper.skip(ViajeroFrecuente::setViajero);
        });
    }

    public ViajeroFrecuente toEntity(ViajeroFrecuenteRequestDto viajeroFrecuenteRequestDto) {
        return modelMapper.map(viajeroFrecuenteRequestDto, ViajeroFrecuente.class);
    }

    public ViajeroFrecuenteResponseDto toResponse(ViajeroFrecuente viajeroFrecuente) {
        return modelMapper.map(viajeroFrecuente, ViajeroFrecuenteResponseDto.class);
    }

    public void updateEntityFromDto(ViajeroFrecuenteRequestDto viajeroFrecuenteRequestDto, ViajeroFrecuente viajeroFrecuente) {
        modelMapper.map(viajeroFrecuenteRequestDto, viajeroFrecuente);
    }
}