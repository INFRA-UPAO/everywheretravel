package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CorreoPersonaRequestDTO;
import com.everywhere.backend.model.dto.CorreoPersonaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CorreoPersonaService {

    List<CorreoPersonaResponseDTO> findAll();
    Optional<CorreoPersonaResponseDTO> findById(Integer id);
    List<CorreoPersonaResponseDTO> findByPersonaId(Integer personaId);
    CorreoPersonaResponseDTO save(CorreoPersonaRequestDTO correoPersonaRequestDTO, Integer personaId);
    CorreoPersonaResponseDTO update(Integer personaId, CorreoPersonaRequestDTO correoPersonaRequestDTO, Integer correoPersonaId);
    void deleteById(Integer id);
}
