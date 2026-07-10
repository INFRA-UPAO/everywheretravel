package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObservacionLiquidacionResponseDTO;

import java.util.List;

public interface ObservacionLiquidacionService {

    List<ObservacionLiquidacionResponseDTO> findAll();
    ObservacionLiquidacionResponseDTO findById(Long id);
    ObservacionLiquidacionResponseDTO save(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO);
    ObservacionLiquidacionResponseDTO update(Long id, ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO);
    void deleteById(Long id);
    List<ObservacionLiquidacionResponseDTO> findByLiquidacionId(Integer liquidacionId);
}
