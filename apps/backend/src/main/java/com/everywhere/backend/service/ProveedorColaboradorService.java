package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ProveedorColaboradorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorColaboradorResponseDTO;

import java.util.List;

public interface ProveedorColaboradorService {

    List<ProveedorColaboradorResponseDTO> findAll();

    ProveedorColaboradorResponseDTO findById(Integer id);

    List<ProveedorColaboradorResponseDTO> findByProveedorId(Integer proveedorId);

    ProveedorColaboradorResponseDTO save(ProveedorColaboradorRequestDTO dto);

    ProveedorColaboradorResponseDTO update(Integer id, ProveedorColaboradorRequestDTO dto);

    void deleteById(Integer id);
}
