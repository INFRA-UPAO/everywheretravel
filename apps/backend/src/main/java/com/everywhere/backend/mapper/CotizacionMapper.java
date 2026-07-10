package com.everywhere.backend.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.Cotizacion;
import lombok.RequiredArgsConstructor;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CotizacionMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMapping() {
        modelMapper.typeMap(CotizacionRequestDto.class, Cotizacion.class).addMappings(mapper -> {
            mapper.skip(Cotizacion::setCounter);
            mapper.skip(Cotizacion::setFormaPago);
            mapper.skip(Cotizacion::setEstadoCotizacion);
            mapper.skip(Cotizacion::setSucursal);
            mapper.skip(Cotizacion::setCarpeta);
        });
    }

    public CotizacionResponseDto toResponse(Cotizacion cotizacion) { 
        return modelMapper.map(cotizacion, CotizacionResponseDto.class);
    }

    public Cotizacion toEntity(CotizacionRequestDto cotizacionRequestDto) { 
        return modelMapper.map(cotizacionRequestDto, Cotizacion.class);
    }
    
    public void updateEntityFromRequest(Cotizacion cotizacion, CotizacionRequestDto cotizacionRequestDto) {
        if (cotizacionRequestDto.getNombreCotizacion() != null) {
            cotizacion.setNombreCotizacion(cotizacionRequestDto.getNombreCotizacion());
        }
        if (cotizacionRequestDto.getCantAdultos() != null) {
            cotizacion.setCantAdultos(cotizacionRequestDto.getCantAdultos());
        }
        if (cotizacionRequestDto.getCantNinos() != null) {
            cotizacion.setCantNinos(cotizacionRequestDto.getCantNinos());
        }
        if (cotizacionRequestDto.getFechaVencimiento() != null) {
            cotizacion.setFechaVencimiento(cotizacionRequestDto.getFechaVencimiento());
        }
        if (cotizacionRequestDto.getOrigenDestino() != null) {
            cotizacion.setOrigenDestino(cotizacionRequestDto.getOrigenDestino());
        }
        if (cotizacionRequestDto.getFechaSalida() != null) {
            cotizacion.setFechaSalida(cotizacionRequestDto.getFechaSalida());
        }
        if (cotizacionRequestDto.getFechaRegreso() != null) {
            cotizacion.setFechaRegreso(cotizacionRequestDto.getFechaRegreso());
        }
        if (cotizacionRequestDto.getMoneda() != null) {
            cotizacion.setMoneda(cotizacionRequestDto.getMoneda());
        }
        if (cotizacionRequestDto.getObservacion() != null) {
            cotizacion.setObservacion(cotizacionRequestDto.getObservacion());
        }
    }

    public CotizacionConDetallesResponseDTO toResponseWithDetalles(CotizacionResponseDto cotizacionResponseDto, 
        List<DetalleCotizacionSimpleDTO> detalleCotizacionSimpleDTOs) {
        CotizacionConDetallesResponseDTO cotizacionConDetallesResponseDTO = modelMapper.map(cotizacionResponseDto, CotizacionConDetallesResponseDTO.class);
        cotizacionConDetallesResponseDTO.setDetalles(detalleCotizacionSimpleDTOs);
        return cotizacionConDetallesResponseDTO;
    }

    public DetalleCotizacionSimpleDTO toDetalleSimple(DetalleCotizacionResponseDto detalleCotizacionResponseDto) {
        return modelMapper.map(detalleCotizacionResponseDto, DetalleCotizacionSimpleDTO.class);
    }
}