package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.PagoPaxRequestDTO;
import com.everywhere.backend.model.dto.PagoPaxResponseDTO;

import java.util.List;

public interface PagoPaxService {

    /**
     * Crea un nuevo pago pax
     */
    PagoPaxResponseDTO create(PagoPaxRequestDTO requestDTO);

    /**
     * Obtiene un pago pax por ID
     */
    PagoPaxResponseDTO findById(Integer id);

    /**
     * Obtiene todos los pagos pax
     */
    List<PagoPaxResponseDTO> findAll();

    /**
     * Obtiene todos los pagos pax de una liquidación específica
     */
    List<PagoPaxResponseDTO> findByLiquidacionId(Integer liquidacionId);

    /**
     * Actualiza un pago pax existente
     */
    PagoPaxResponseDTO update(Integer id, PagoPaxRequestDTO requestDTO);

    /**
     * Elimina un pago pax por ID
     */
    void delete(Integer id);
}
