package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.SucursalRequestDTO;
import com.everywhere.backend.model.dto.SucursalResponseDTO;

import java.util.List;

public interface SucursalService {
    List<SucursalResponseDTO> findAll();
    SucursalResponseDTO findById(Integer id);
    List<SucursalResponseDTO> findByDescripcion(String descripcion);
    SucursalResponseDTO findByDescripcionExacta(String descripcion);
    List<SucursalResponseDTO> findByEstado(Boolean estado);
    List<SucursalResponseDTO> findByEstadoAndDescripcion(Boolean estado, String descripcion);
    List<SucursalResponseDTO> findByDireccion(String direccion);
    SucursalResponseDTO findByEmail(String email);
    SucursalResponseDTO save(SucursalRequestDTO sucursalRequestDTO);
    SucursalResponseDTO update(Integer id, SucursalRequestDTO sucursalRequestDTO);
    void deleteById(Integer id);
    SucursalResponseDTO cambiarEstado(Integer id, Boolean estado);
}
