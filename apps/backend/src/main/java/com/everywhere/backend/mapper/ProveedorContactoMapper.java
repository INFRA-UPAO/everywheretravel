package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ProveedorContactoRequestDTO;
import com.everywhere.backend.model.dto.ProveedorContactoResponseDTO;
import com.everywhere.backend.model.entity.ProveedorContacto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProveedorContactoMapper {

    private final ModelMapper modelMapper;

    public ProveedorContactoResponseDTO toResponseDTO(ProveedorContacto entity) {
        ProveedorContactoResponseDTO dto = modelMapper.map(entity, ProveedorContactoResponseDTO.class);
        if (entity.getProveedor() != null) {
            dto.setProveedorId(entity.getProveedor().getId());
            dto.setProveedorNombre(entity.getProveedor().getNombre());
        }
        if (entity.getGrupoContacto() != null) {
            dto.setGrupoContactoId(entity.getGrupoContacto().getId());
            dto.setGrupoContactoNombre(entity.getGrupoContacto().getNombre());
        }
        return dto;
    }

    public ProveedorContacto toEntity(ProveedorContactoRequestDTO dto) {
        return modelMapper.map(dto, ProveedorContacto.class);
    }

    public void updateEntityFromDTO(ProveedorContactoRequestDTO dto, ProveedorContacto entity) {
        modelMapper.map(dto, entity);
    }
}
