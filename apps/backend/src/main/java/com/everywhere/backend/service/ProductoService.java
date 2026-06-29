package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {

    ProductoResponseDTO create(ProductoRequestDTO productoRequestDTO);
    ProductoResponseDTO update(Integer id, ProductoRequestDTO productoRequestDTO);
    ProductoResponseDTO getById(Integer id);
    List<ProductoResponseDTO> getAll();
    void delete(Integer id);
}
