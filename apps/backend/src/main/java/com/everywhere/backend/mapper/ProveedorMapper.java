package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ProveedorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorResponseDTO;
import com.everywhere.backend.model.entity.Proveedor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Proveedor toEntity(ProveedorRequestDTO proveedorRequestDTO) {
        return modelMapper.map(proveedorRequestDTO, Proveedor.class);
    }

    public ProveedorResponseDTO toResponseDTO(Proveedor proveedor) {
        return modelMapper.map(proveedor, ProveedorResponseDTO.class);
    }

    public void updateEntityFromDTO(ProveedorRequestDTO proveedorRequestDTO, Proveedor proveedor) {
        modelMapper.map(proveedorRequestDTO, proveedor);
    }
}
