package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.LiquidacionRequestDTO;
import com.everywhere.backend.model.dto.LiquidacionResponseDTO;
import com.everywhere.backend.model.entity.Liquidacion;

import jakarta.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LiquidacionMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMapping() {
        modelMapper.typeMap(LiquidacionRequestDTO.class, Liquidacion.class).addMappings(mapper -> {
            mapper.skip(Liquidacion::setCotizacion);
            mapper.skip(Liquidacion::setProducto);
            mapper.skip(Liquidacion::setFormaPago);
            mapper.skip(Liquidacion::setCarpeta);
        });
    }

    public LiquidacionResponseDTO toResponseDTO(Liquidacion liquidacion) {
        LiquidacionResponseDTO liquidacionResponseDTO = modelMapper.map(liquidacion, LiquidacionResponseDTO.class);
        return liquidacionResponseDTO;
    }

    public Liquidacion toEntity(LiquidacionRequestDTO liquidacionRequestDTO) {
        Liquidacion liquidacion = modelMapper.map(liquidacionRequestDTO, Liquidacion.class);
        return liquidacion;
    }

    public void updateEntityFromRequest(Liquidacion liquidacion, LiquidacionRequestDTO liquidacionRequestDTO) {
        modelMapper.map(liquidacionRequestDTO, liquidacion);
    }
}