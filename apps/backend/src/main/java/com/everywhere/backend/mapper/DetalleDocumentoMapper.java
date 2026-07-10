package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;
import com.everywhere.backend.model.entity.DetalleDocumento; 

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DetalleDocumentoMapper {
    
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMappings() {
        modelMapper.typeMap(DetalleDocumentoRequestDto.class, DetalleDocumento.class).addMappings(mapper -> {
            mapper.skip(DetalleDocumento::setDocumento);
            mapper.skip(DetalleDocumento::setPersonaNatural);
        });
    }

    public DetalleDocumento toEntity(DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        DetalleDocumento detalleDocumento = modelMapper.map(detalleDocumentoRequestDto, DetalleDocumento.class);
        return detalleDocumento;
    }

    public DetalleDocumentoResponseDto toResponse(DetalleDocumento detalleDocumento) {
        DetalleDocumentoResponseDto detalleDocumentoResponseDto = modelMapper.map(detalleDocumento, DetalleDocumentoResponseDto.class);
        return detalleDocumentoResponseDto;
    }

    public void updateEntityFromDto(DetalleDocumentoRequestDto detalleDocumentoRequestDto, DetalleDocumento detalleDocumento) {
        modelMapper.map(detalleDocumentoRequestDto, detalleDocumento);
    }
}