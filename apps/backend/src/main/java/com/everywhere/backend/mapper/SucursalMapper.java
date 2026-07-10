package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.SucursalRequestDTO;
import com.everywhere.backend.model.dto.SucursalResponseDTO;
import com.everywhere.backend.model.entity.Sucursal;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SucursalMapper {

    @Autowired
    private ModelMapper modelMapper;
    public Sucursal toEntity(SucursalRequestDTO sucursalRequestDTO) {
        return modelMapper.map(sucursalRequestDTO, Sucursal.class);
    }
    public SucursalResponseDTO toResponseDTO(Sucursal sucursal) {
        return modelMapper.map(sucursal, SucursalResponseDTO.class);
    }

    public void updateEntityFromDTO(SucursalRequestDTO sucursalRequestDTO, Sucursal sucursal) {
        modelMapper.map(sucursalRequestDTO, sucursal);
    }


}
