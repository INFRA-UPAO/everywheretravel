package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ProveedorGrupoContactoRequestDTO;
import com.everywhere.backend.model.dto.ProveedorGrupoContactoResponseDTO;
import com.everywhere.backend.model.entity.ProveedorGrupoContacto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProveedorGrupoContactoMapper {

    private final ModelMapper modelMapper;

    public ProveedorGrupoContactoResponseDTO toResponseDTO(ProveedorGrupoContacto entity) {
        return modelMapper.map(entity, ProveedorGrupoContactoResponseDTO.class);
    }

    public ProveedorGrupoContacto toEntity(ProveedorGrupoContactoRequestDTO dto) {
        return modelMapper.map(dto, ProveedorGrupoContacto.class);
    }

    public void updateEntityFromDTO(ProveedorGrupoContactoRequestDTO dto, ProveedorGrupoContacto entity) {
        modelMapper.map(dto, entity);
    }
}
