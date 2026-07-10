package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.CorreoPersonaRequestDTO;
import com.everywhere.backend.model.dto.CorreoPersonaResponseDTO;
import com.everywhere.backend.model.entity.CorreoPersona;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CorreoPersonaMapper {

    private final ModelMapper modelMapper;

    public CorreoPersona toEntity(CorreoPersonaRequestDTO correoPersona) {
        return modelMapper.map(correoPersona, CorreoPersona.class);
    }

    public CorreoPersonaResponseDTO toResponseDTO(CorreoPersona correoPersona) {
        return modelMapper.map(correoPersona, CorreoPersonaResponseDTO.class);
    }

    public void updateEntityFromDTO(CorreoPersona correoPersona, CorreoPersonaRequestDTO correoPersonaRequestDTO) {
        modelMapper.map(correoPersonaRequestDTO, correoPersona);
    }
}
