package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ProveedorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorResponseDTO;

import java.util.List;

public interface ProveedorService {

    ProveedorResponseDTO create(ProveedorRequestDTO proveedorRequestDTO);
    ProveedorResponseDTO update(Integer id, ProveedorRequestDTO proveedorRequestDTO);
    ProveedorResponseDTO getById(Integer id);
    List<ProveedorResponseDTO> getAll();
    void delete(Integer id);
}
