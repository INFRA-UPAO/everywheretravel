package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DetalleReciboRequestDTO;
import com.everywhere.backend.model.dto.DetalleReciboResponseDTO;

import java.util.List;

public interface DetalleReciboService {
    List<DetalleReciboResponseDTO> findAll();
    DetalleReciboResponseDTO findById(Integer id);
    List<DetalleReciboResponseDTO> findByReciboId(Integer reciboId);
    DetalleReciboResponseDTO save(DetalleReciboRequestDTO detalleReciboRequestDTO);
    DetalleReciboResponseDTO patch(Integer id, DetalleReciboRequestDTO detalleReciboRequestDTO);
    void deleteById(Integer id);
}
