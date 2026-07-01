package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.NaturalJuridicoResponseDTO;
import com.everywhere.backend.model.entity.NaturalJuridico;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NaturalJuridicoMapper {

    private final ModelMapper modelMapper;
    private final PersonaNaturalMapper personaNaturalMapper;
    private final PersonaJuridicaMapper personaJuridicaMapper;

    public NaturalJuridicoResponseDTO toResponseDTO(NaturalJuridico naturalJuridico) {
        NaturalJuridicoResponseDTO dto = modelMapper.map(naturalJuridico, NaturalJuridicoResponseDTO.class);
        if (naturalJuridico.getPersonaNatural() != null)
            dto.setPersonaNatural(personaNaturalMapper.toResponseDTO(naturalJuridico.getPersonaNatural()));
        if (naturalJuridico.getPersonaJuridica() != null)
            dto.setPersonaJuridica(personaJuridicaMapper.toResponseDTO(naturalJuridico.getPersonaJuridica()));
        return dto;
    }
}