package com.everywhere.backend.mapper;

import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.CarpetaRequestDto;
import com.everywhere.backend.model.dto.CarpetaResponseDto;
import com.everywhere.backend.model.entity.Carpeta;

import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CarpetaMapper {

    private final ModelMapper modelMapper; 

    public Carpeta toEntity(CarpetaRequestDto carpetaRequestDto) {
        return modelMapper.map(carpetaRequestDto, Carpeta.class);
    }

    public CarpetaResponseDto toResponse(Carpeta carpeta) {
        return modelMapper.map(carpeta, CarpetaResponseDto.class);
    }

    public void updateEntityFromRequest(CarpetaRequestDto carpetaRequestDto, Carpeta carpeta) {
        modelMapper.map(carpetaRequestDto, carpeta);
    }
}