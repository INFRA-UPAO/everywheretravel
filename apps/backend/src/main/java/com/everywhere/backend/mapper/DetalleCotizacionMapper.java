package com.everywhere.backend.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.DetalleCotizacionRequestDto;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.model.entity.DetalleCotizacion;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DetalleCotizacionMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMappings() {
        modelMapper.typeMap(DetalleCotizacionRequestDto.class, DetalleCotizacion.class).addMappings(mapper -> {
                mapper.skip(DetalleCotizacion::setCotizacion);
                mapper.skip(DetalleCotizacion::setProducto);
                mapper.skip(DetalleCotizacion::setProveedor);
                mapper.skip(DetalleCotizacion::setOperador);
            });
    }

    public DetalleCotizacionResponseDto toResponse(DetalleCotizacion detalleCotizacion) {
        DetalleCotizacionResponseDto detalleCotizacionResponseDto = modelMapper.map(detalleCotizacion, DetalleCotizacionResponseDto.class);
        return detalleCotizacionResponseDto;
    }

    public DetalleCotizacion toEntity(DetalleCotizacionRequestDto detalleCotizacionRequestDto) {
        DetalleCotizacion detalleCotizacion = modelMapper.map(detalleCotizacionRequestDto, DetalleCotizacion.class);
        return detalleCotizacion;
    }

    public void updateEntityFromRequest(DetalleCotizacion detalleCotizacion, DetalleCotizacionRequestDto detalleCotizacionRequestDto) {
        if (detalleCotizacionRequestDto.getCantidad() != null) {
            detalleCotizacion.setCantidad(detalleCotizacionRequestDto.getCantidad());
        }
        if (detalleCotizacionRequestDto.getUnidad() != null) {
            detalleCotizacion.setUnidad(detalleCotizacionRequestDto.getUnidad());
        }
        if (detalleCotizacionRequestDto.getDescripcion() != null) {
            detalleCotizacion.setDescripcion(detalleCotizacionRequestDto.getDescripcion());
        }
        if (detalleCotizacionRequestDto.getComision() != null) {
            detalleCotizacion.setComision(detalleCotizacionRequestDto.getComision());
        }
        if (detalleCotizacionRequestDto.getPrecioHistorico() != null) {
            detalleCotizacion.setPrecioHistorico(detalleCotizacionRequestDto.getPrecioHistorico());
        }
        if (detalleCotizacionRequestDto.getSeleccionado() != null) {
            detalleCotizacion.setSeleccionado(detalleCotizacionRequestDto.getSeleccionado());
        }
    }
}