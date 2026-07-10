package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ProveedorGrupoContactoRequestDTO;
import com.everywhere.backend.model.dto.ProveedorGrupoContactoResponseDTO;

import java.util.List;

public interface ProveedorGrupoContactoService {

    List<ProveedorGrupoContactoResponseDTO> findAll();

    ProveedorGrupoContactoResponseDTO findById(Integer id);

    List<ProveedorGrupoContactoResponseDTO> findByNombre(String nombre);

    ProveedorGrupoContactoResponseDTO save(ProveedorGrupoContactoRequestDTO dto);

    ProveedorGrupoContactoResponseDTO update(Integer id, ProveedorGrupoContactoRequestDTO dto);

    void deleteById(Integer id);
}
