package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.TelefonoPersonaRequestDTO;
import com.everywhere.backend.model.dto.TelefonoPersonaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface TelefonoPersonaService {
    List<TelefonoPersonaResponseDTO> findAll();
    Optional<TelefonoPersonaResponseDTO> findById(Integer telefonoId, Integer personaId);
    List<TelefonoPersonaResponseDTO> findByPersonaId(Integer personaId);
    TelefonoPersonaResponseDTO save(TelefonoPersonaRequestDTO telefonoPersonaRequestDTO, Integer personaId);
    TelefonoPersonaResponseDTO update(Integer personaId, TelefonoPersonaRequestDTO TelefonoPersonaRequestDTO, Integer TelefonoPersonaId);
    void deleteById(Integer telefonoId, Integer personaId);
}
