package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.CategoriaPersonaRequestDTO;
import com.everywhere.backend.model.dto.CategoriaPersonaResponseDTO;
import com.everywhere.backend.model.entity.CategoriaPersona;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoriaPersonaMapper {
    private final ModelMapper modelMapper;

    public CategoriaPersonaResponseDTO toResponseDTO(CategoriaPersona categoriaPersona) {
        return modelMapper.map(categoriaPersona, CategoriaPersonaResponseDTO.class);
    }

    public CategoriaPersona toEntity(CategoriaPersonaRequestDTO categoriaPersonaRequestDTO) {
        return modelMapper.map(categoriaPersonaRequestDTO, CategoriaPersona.class);
    }

    public void updateEntityFromDTO(CategoriaPersonaRequestDTO categoriaPersonaRequestDTO, CategoriaPersona categoriaPersona) {
        modelMapper.map(categoriaPersonaRequestDTO, categoriaPersona);
    }
}