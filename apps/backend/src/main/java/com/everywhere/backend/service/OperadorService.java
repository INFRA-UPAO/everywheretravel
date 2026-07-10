package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.OperadorRequestDTO;
import com.everywhere.backend.model.dto.OperadorResponseDTO;

import java.util.List;

public interface OperadorService {

    List<OperadorResponseDTO> findAll();
    OperadorResponseDTO findByNombre(String nombre);
    OperadorResponseDTO findById(int id);
    OperadorResponseDTO save(OperadorRequestDTO dto);
    OperadorResponseDTO update(int id, OperadorRequestDTO dto);
    void deleteById(int id);
}
