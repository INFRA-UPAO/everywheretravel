package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.PersonaRequestDTO;
import com.everywhere.backend.model.dto.PersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaDisplayDto;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Personas;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PersonaMapper {

    private final ModelMapper modelMapper;
    private final TelefonoPersonaMapper telefonoPersonaMapper;

    public PersonaResponseDTO toResponseDTO(Personas persona) {
        PersonaResponseDTO personaResponseDTO = modelMapper.map(persona, PersonaResponseDTO.class);

        if (persona.getTelefonos() != null) {
            personaResponseDTO.setTelefonos(
                    persona.getTelefonos().stream()
                            .map(telefonoPersonaMapper::toResponseDTO)
                            .collect(Collectors.toList())
            );
        }

        return personaResponseDTO;
    }

    public Personas toEntity(PersonaRequestDTO personaRequestDTO) {
        return modelMapper.map(personaRequestDTO, Personas.class);
    }

    public void updateEntityFromDTO(PersonaRequestDTO personaRequestDTO, Personas personas) {
        modelMapper.map(personaRequestDTO, personas);
    }

    public PersonaDisplayDto toDisplayDTO(PersonaNatural personaNatural) {
        String nombreCompleto = personaNatural.getNombres() + " " +
                personaNatural.getApellidosPaterno() + " " +
                personaNatural.getApellidosMaterno();
        return new PersonaDisplayDto(
                personaNatural.getId(),
                "NATURAL",
                String.valueOf(personaNatural.getDocumento()),
                nombreCompleto
        );
    }

    public PersonaDisplayDto toDisplayDTO(PersonaJuridica personaJuridica) {
        return new PersonaDisplayDto(
                personaJuridica.getId(),
                "JURIDICA",
                String.valueOf(personaJuridica.getRuc()),
                personaJuridica.getRazonSocial()
        );
    }
}
