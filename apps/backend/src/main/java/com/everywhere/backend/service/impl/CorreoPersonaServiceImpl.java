package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CorreoPersonaMapper;
import com.everywhere.backend.model.dto.CorreoPersonaRequestDTO;
import com.everywhere.backend.model.dto.CorreoPersonaResponseDTO;
import com.everywhere.backend.model.entity.CorreoPersona;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.repository.CorreoPersonaRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.service.CorreoPersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CorreoPersonaServiceImpl implements CorreoPersonaService {

    private final CorreoPersonaRepository correoPersonaRepository;
    private final CorreoPersonaMapper correoPersonaMapper;
    private final PersonaRepository personaRepository;

    @Override
    public List<CorreoPersonaResponseDTO> findAll() {
        return correoPersonaRepository.findAll()
                .stream()
                .map(correoPersonaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Optional<CorreoPersonaResponseDTO> findById(Integer id) {
        return correoPersonaRepository.findById(id)
                .map(correoPersonaMapper::toResponseDTO);
    }

    @Override
    public List<CorreoPersonaResponseDTO> findByPersonaId(Integer personaId) {
        return correoPersonaRepository.findByPersonaId(personaId)
                .stream()
                .map(correoPersonaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public CorreoPersonaResponseDTO save(CorreoPersonaRequestDTO correoPersonaRequestDTO, Integer personaId) {
        Personas persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + personaId));

        CorreoPersona correoPersona = correoPersonaMapper.toEntity(correoPersonaRequestDTO);
        correoPersona.setPersona(persona);

        return correoPersonaMapper.toResponseDTO(correoPersonaRepository.save(correoPersona));
    }


    @Override
    public CorreoPersonaResponseDTO update(Integer personaId, CorreoPersonaRequestDTO correoPersonaRequestDTO, Integer correoPersonaId) {
        CorreoPersona correo = correoPersonaRepository.findById(correoPersonaId)
                .orElseThrow(() -> new RuntimeException("Correo no encontrado con ID: " + correoPersonaId));

        Personas persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + personaId));

        correoPersonaMapper.updateEntityFromDTO(correo, correoPersonaRequestDTO);
        correo.setPersona(persona);
        return correoPersonaMapper.toResponseDTO(correoPersonaRepository.save(correo));
    }


    @Override
    public void deleteById(Integer id) {
        if (!correoPersonaRepository.existsById(id)) {
            throw new RuntimeException("Correo no encontrado con ID: " + id);
        }
        correoPersonaRepository.deleteById(id);
    }
}
