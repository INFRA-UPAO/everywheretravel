package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.PersonaJuridicaRequestDTO;
import com.everywhere.backend.model.dto.PersonaJuridicaResponseDTO;
import com.everywhere.backend.model.entity.PersonaJuridica;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonaJuridicaMapper {

    private final ModelMapper modelMapper;
    private final PersonaMapper personaMapper;

    public PersonaJuridicaResponseDTO toResponseDTO(PersonaJuridica personaJuridica) {
        PersonaJuridicaResponseDTO personaJuridicaResponseDTO = modelMapper.map(personaJuridica, PersonaJuridicaResponseDTO.class);
        if (personaJuridica.getPersonas() != null) {
            personaJuridicaResponseDTO.setPersona(personaMapper.toResponseDTO(personaJuridica.getPersonas()));
        }
        return personaJuridicaResponseDTO;
    }

    public PersonaJuridica toEntity(PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        return modelMapper.map(personaJuridicaRequestDTO, PersonaJuridica.class);
    }

    public void updateEntityFromDTO(PersonaJuridicaRequestDTO personaJuridicaRequestDTO, PersonaJuridica personaJuridica) {
        modelMapper.map(personaJuridicaRequestDTO, personaJuridica);

        // Actualizar persona base si existe
        if (personaJuridicaRequestDTO.getPersona() != null && personaJuridica.getPersonas() != null) {
            personaMapper.updateEntityFromDTO(personaJuridicaRequestDTO.getPersona(), personaJuridica.getPersonas());
        }
    }
}
