package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ProveedorColaboradorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorColaboradorResponseDTO;
import com.everywhere.backend.model.entity.ProveedorColaborador;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProveedorColaboradorMapper {

    private final ModelMapper modelMapper;

    public ProveedorColaboradorResponseDTO toResponseDTO(ProveedorColaborador entity) {
        ProveedorColaboradorResponseDTO dto = modelMapper.map(entity, ProveedorColaboradorResponseDTO.class);
        if (entity.getProveedor() != null) {
            dto.setProveedorId(entity.getProveedor().getId());
            dto.setProveedorNombre(entity.getProveedor().getNombre());
        }
        return dto;
    }

    public ProveedorColaborador toEntity(ProveedorColaboradorRequestDTO dto) {
        return modelMapper.map(dto, ProveedorColaborador.class);
    }

    public void updateEntityFromDTO(ProveedorColaboradorRequestDTO dto, ProveedorColaborador entity) {
        modelMapper.map(dto, entity);
    }
}
