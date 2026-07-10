package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.dto.PersonaNaturalSinViajeroResponseDTO;
import com.everywhere.backend.model.entity.PersonaNatural;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonaNaturalMapper {

    private final ModelMapper modelMapper;
    private final PersonaMapper personaMapper;
    private final ViajeroMapper viajeroMapper;
    private final CategoriaPersonaMapper categoriaPersonaMapper; // ✅ Agregado

    public PersonaNaturalResponseDTO toResponseDTO(PersonaNatural personaNatural) {
        PersonaNaturalResponseDTO personaNaturalResponseDTO = modelMapper.map(personaNatural, PersonaNaturalResponseDTO.class);

        // ✅ Corregido: getPersonas() en lugar de getPersona()
        if (personaNatural.getPersonas() != null) {
            personaNaturalResponseDTO.setPersona(personaMapper.toResponseDTO(personaNatural.getPersonas()));
        }

        if (personaNatural.getViajero() != null) {
            personaNaturalResponseDTO.setViajero(viajeroMapper.toResponseDTO(personaNatural.getViajero()));
        }

        return personaNaturalResponseDTO;
    }

    public PersonaNatural toEntity(PersonaNaturalRequestDTO personaNaturalRequestDTOdto) {
        return modelMapper.map(personaNaturalRequestDTOdto, PersonaNatural.class);
    }

    public void updateEntityFromDTO(PersonaNaturalRequestDTO personaNaturalRequestDTO, PersonaNatural personaNatural) {
        modelMapper.map(personaNaturalRequestDTO, personaNatural);

        if (personaNaturalRequestDTO.getPersona() != null && personaNatural.getPersonas() != null)
            personaMapper.updateEntityFromDTO(personaNaturalRequestDTO.getPersona(), personaNatural.getPersonas());
    }

    public PersonaNaturalSinViajeroResponseDTO toSinViajeroResponseDTO(PersonaNatural personaNatural) {
        PersonaNaturalSinViajeroResponseDTO dto = modelMapper.map(personaNatural, PersonaNaturalSinViajeroResponseDTO.class);

        if (personaNatural.getPersonas() != null) {
            dto.setPersona(personaMapper.toResponseDTO(personaNatural.getPersonas()));
        }

        if (personaNatural.getCategoriaPersona() != null) {
            dto.setCategoriaPersona(categoriaPersonaMapper.toResponseDTO(personaNatural.getCategoriaPersona()));
        }

        return dto;
    }
}