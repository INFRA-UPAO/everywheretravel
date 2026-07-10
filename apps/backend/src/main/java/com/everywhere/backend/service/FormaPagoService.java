package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.FormaPagoRequestDTO;
import com.everywhere.backend.model.dto.FormaPagoResponseDTO;

import java.util.List;

public interface FormaPagoService {
    List<FormaPagoResponseDTO> findAll();
    FormaPagoResponseDTO findById(Integer id);
    FormaPagoResponseDTO findByCodigo(Integer codigo);
    List<FormaPagoResponseDTO> findByDescripcion(String descripcion);
    FormaPagoResponseDTO save(FormaPagoRequestDTO formaPagoRequestDTO);
    FormaPagoResponseDTO update(Integer id, FormaPagoRequestDTO formaPagoRequestDTO);
    void deleteById(Integer id);
}
