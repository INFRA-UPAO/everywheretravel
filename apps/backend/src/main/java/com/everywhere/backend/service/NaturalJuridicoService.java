package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.NaturalJuridicoRequestDTO;
import com.everywhere.backend.model.dto.NaturalJuridicoResponseDTO;
import com.everywhere.backend.model.dto.NaturalJuridicoPatchDTO;

import java.util.List;

public interface NaturalJuridicoService {
    
    List<NaturalJuridicoResponseDTO> crearRelaciones(NaturalJuridicoRequestDTO naturalJuridicoRequestDTO);
    List<NaturalJuridicoResponseDTO> findByPersonaNaturalId(Integer personaNaturalId);
    List<NaturalJuridicoResponseDTO> findByPersonaJuridicaId(Integer personaJuridicaId);
    NaturalJuridicoResponseDTO findById(Integer id);
    void deleteById(Integer id);
    void deleteByPersonas(Integer personaNaturalId, Integer personaJuridicaId);
    List<NaturalJuridicoResponseDTO> findAll();
    List<NaturalJuridicoResponseDTO> patchRelacionesPersonaNatural(Integer personaNaturalId, NaturalJuridicoPatchDTO naturalJuridicoPatchDTO);
}