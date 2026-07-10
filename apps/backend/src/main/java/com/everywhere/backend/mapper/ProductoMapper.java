package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO;
import com.everywhere.backend.model.entity.Producto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@RequiredArgsConstructor
public class ProductoMapper {

    private final ModelMapper modelMapper;

    public ProductoResponseDTO toResponseDTO(Producto producto) {
        return modelMapper.map(producto, ProductoResponseDTO.class);
    }

    public Producto toEntity(ProductoRequestDTO productoRequestDTO) {
        Producto producto = modelMapper.map(productoRequestDTO, Producto.class);
        producto.setCreado(LocalDateTime.now());
        return producto;
    }

    public void updateEntityFromDTO(ProductoRequestDTO productoRequestDTO, Producto producto) {
        modelMapper.map(productoRequestDTO, producto);
    }
}
