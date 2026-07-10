package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ProveedorContactoRequestDTO;
import com.everywhere.backend.model.dto.ProveedorContactoResponseDTO;

import java.util.List;

public interface ProveedorContactoService {

    List<ProveedorContactoResponseDTO> findAll();

    ProveedorContactoResponseDTO findById(Integer id);

    List<ProveedorContactoResponseDTO> findByProveedorId(Integer proveedorId);

    List<ProveedorContactoResponseDTO> findByGrupoContactoId(Integer grupoContactoId);

    ProveedorContactoResponseDTO save(ProveedorContactoRequestDTO dto);

    ProveedorContactoResponseDTO update(Integer id, ProveedorContactoRequestDTO dto);

    void deleteById(Integer id);
}
