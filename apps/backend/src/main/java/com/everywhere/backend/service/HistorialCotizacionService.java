package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.HistorialCotizacionRequestDTO;
import com.everywhere.backend.model.dto.HistorialCotizacionResponseDTO;
import com.everywhere.backend.model.dto.HistorialCotizacionSimpleDTO;

import java.util.List;

public interface HistorialCotizacionService {

    List<HistorialCotizacionResponseDTO> findAll();

    HistorialCotizacionResponseDTO findById(Integer id);

    List<HistorialCotizacionSimpleDTO> findByCotizacionId(Integer cotizacionId);

    HistorialCotizacionResponseDTO save(HistorialCotizacionRequestDTO historialCotizacionRequestDTO);

    HistorialCotizacionResponseDTO update(Integer id, HistorialCotizacionRequestDTO historialCotizacionRequestDTO);

    void deleteById(Integer id);

    HistorialCotizacionResponseDTO registrarCambioEstado(Integer cotizacionId,
                                                         Integer estadoCotizacionId,
                                                         String observacion);
}