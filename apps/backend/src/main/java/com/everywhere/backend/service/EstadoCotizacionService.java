package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.EstadoCotizacionRequestDTO;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDTO;

import java.util.List; 

public interface EstadoCotizacionService {

    EstadoCotizacionResponseDTO create(EstadoCotizacionRequestDTO dto);

    EstadoCotizacionResponseDTO update(Integer id, EstadoCotizacionRequestDTO dto);

    EstadoCotizacionResponseDTO getById(Integer id);

    List<EstadoCotizacionResponseDTO> getAll();

    void delete(Integer id);
}

