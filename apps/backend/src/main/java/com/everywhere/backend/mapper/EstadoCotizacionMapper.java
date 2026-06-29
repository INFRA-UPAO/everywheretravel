package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.EstadoCotizacionRequestDTO;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDTO;
import com.everywhere.backend.model.entity.EstadoCotizacion;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EstadoCotizacionMapper {

    @Autowired
    private ModelMapper modelMapper;
    public EstadoCotizacion toEntity(EstadoCotizacionRequestDTO estadoCotizacionRequestDTO) {
        return modelMapper.map(estadoCotizacionRequestDTO, EstadoCotizacion.class);
    }
        public EstadoCotizacionResponseDTO toResponseDTO(EstadoCotizacion estadoCotizacion) {
        return modelMapper.map(estadoCotizacion, EstadoCotizacionResponseDTO.class);
    }
        public void updateEntityFromDTO(EstadoCotizacionRequestDTO estadoCotizacionRequestDTO, EstadoCotizacion estadoCotizacion) {
        modelMapper.map(estadoCotizacionRequestDTO, estadoCotizacion);
    }
}
