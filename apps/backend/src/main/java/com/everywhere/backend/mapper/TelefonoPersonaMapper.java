package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.TelefonoPersonaRequestDTO;
import com.everywhere.backend.model.dto.TelefonoPersonaResponseDTO;
import com.everywhere.backend.model.entity.TelefonoPersona;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelefonoPersonaMapper {

    private final ModelMapper modelMapper;

    public TelefonoPersona toEntity(TelefonoPersonaRequestDTO telefonoPersonaRequestDTO) {
        return modelMapper.map(telefonoPersonaRequestDTO, TelefonoPersona.class);
    }

    public TelefonoPersonaResponseDTO toResponseDTO(TelefonoPersona telefonoPersona) {
        return modelMapper.map(telefonoPersona, TelefonoPersonaResponseDTO.class);
    }

    public void updateEntityFromDTO(TelefonoPersonaRequestDTO telefonoPersonaRequestDTO, TelefonoPersona telefonoPersona) {
        modelMapper.map(telefonoPersonaRequestDTO, telefonoPersona);
    }
}
