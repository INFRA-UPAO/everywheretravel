package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.PersonaJuridicaRequestDTO;
import com.everywhere.backend.model.dto.PersonaJuridicaResponseDTO;

import java.util.List;

public interface PersonaJuridicaService {
    List<PersonaJuridicaResponseDTO> findAll();
    PersonaJuridicaResponseDTO findById(Integer id);
    List<PersonaJuridicaResponseDTO> findByRuc(String ruc);
    List<PersonaJuridicaResponseDTO> findByRazonSocial(String razonSocial);
    PersonaJuridicaResponseDTO save(PersonaJuridicaRequestDTO personaJuridicaRequestDTO);
    PersonaJuridicaResponseDTO patch(Integer id, PersonaJuridicaRequestDTO personaJuridicaRequestDTO);
    void deleteById(Integer id);
}
