package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.TelefonoPersonaMapper;
import com.everywhere.backend.model.dto.TelefonoPersonaRequestDTO;
import com.everywhere.backend.model.dto.TelefonoPersonaResponseDTO;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.entity.TelefonoPersona;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.repository.TelefonoPersonaRepository;
import com.everywhere.backend.service.TelefonoPersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelefonoPersonaServiceImpl implements TelefonoPersonaService {

    private final TelefonoPersonaRepository telefonoPersonaRepository;
    private final PersonaRepository personaRepository;
    private final TelefonoPersonaMapper telefonoPersonaMapper;

    @Override
    public List<TelefonoPersonaResponseDTO> findAll() {
        return telefonoPersonaRepository.findAll()
                .stream().map(telefonoPersonaMapper::toResponseDTO).toList();
    }

    @Override
    public Optional<TelefonoPersonaResponseDTO> findById(Integer telefonoId, Integer personaId) {
        return telefonoPersonaRepository.findByIdAndPersonaId(telefonoId, personaId)
                .map(telefonoPersonaMapper::toResponseDTO);
    }

    @Override
    public List<TelefonoPersonaResponseDTO> findByPersonaId(Integer personaId) {
        return telefonoPersonaRepository.findByPersonaId(personaId)
                .stream()
                .map(telefonoPersonaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public TelefonoPersonaResponseDTO save(TelefonoPersonaRequestDTO telefonoPersonaRequestDTO, Integer personaId) {
        Personas persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + personaId));

        TelefonoPersona telefono = telefonoPersonaMapper.toEntity(telefonoPersonaRequestDTO);
        telefono.setPersona(persona);
        return telefonoPersonaMapper.toResponseDTO(telefonoPersonaRepository.save(telefono));
    }



    @Override
    public TelefonoPersonaResponseDTO update(Integer personaId, TelefonoPersonaRequestDTO telefonoPersonaRequestDTO, Integer telefonoId) {
        TelefonoPersona telefono = telefonoPersonaRepository.findByIdAndPersonaId(telefonoId, personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Teléfono no encontrado con ID: " + telefonoId + " para la persona con ID: "+ personaId));

        telefonoPersonaMapper.updateEntityFromDTO(telefonoPersonaRequestDTO, telefono);
        return telefonoPersonaMapper.toResponseDTO(telefonoPersonaRepository.save(telefono));
    }


    @Override
    public void deleteById(Integer telefonoId, Integer personaId) {
        if (!telefonoPersonaRepository.existsByIdAndPersonaId(telefonoId, personaId))
            throw new ResourceNotFoundException("Teléfono no encontrado con ID: " + telefonoId + " para la persona con ID: " + personaId);
        telefonoPersonaRepository.deleteById(telefonoId);
    }
}
