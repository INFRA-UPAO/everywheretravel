package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ViajeroConPersonaResponseDTO;
import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;

import java.util.List;

public interface ViajeroService {

    List<ViajeroResponseDTO> findAll();
    ViajeroResponseDTO findById(Integer id);
    List<ViajeroResponseDTO> findByNacionalidad(String nacionalidad);
    List<ViajeroResponseDTO> findByResidencia(String residencia);
    ViajeroResponseDTO save(ViajeroRequestDTO viajeroRequestDTO);
    ViajeroResponseDTO patch(Integer id, ViajeroRequestDTO viajeroRequestDTO);
    void deleteById(Integer id);
    List<ViajeroConPersonaResponseDTO> findAllWithPersonaNatural();
}