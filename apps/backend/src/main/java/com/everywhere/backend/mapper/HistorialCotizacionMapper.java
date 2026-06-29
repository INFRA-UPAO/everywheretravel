package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.HistorialCotizacionRequestDTO;
import com.everywhere.backend.model.dto.HistorialCotizacionResponseDTO;
import com.everywhere.backend.model.dto.HistorialCotizacionSimpleDTO;
import com.everywhere.backend.model.entity.HistorialCotizacion;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HistorialCotizacionMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMapping() {
        modelMapper.typeMap(HistorialCotizacionRequestDTO.class, HistorialCotizacion.class)
                .addMappings(mapper -> {
                    mapper.skip(HistorialCotizacion::setId);
                    mapper.skip(HistorialCotizacion::setUuid);
                    mapper.skip(HistorialCotizacion::setFechaCreacion);
                    mapper.skip(HistorialCotizacion::setUsuario);
                    mapper.skip(HistorialCotizacion::setCotizacion);
                    mapper.skip(HistorialCotizacion::setEstadoCotizacion);
                });
    }

    public HistorialCotizacion toEntity(HistorialCotizacionRequestDTO historialCotizacionRequestDTO) {
        HistorialCotizacion historialCotizacion = new HistorialCotizacion();
        updateEntityFromDTO(historialCotizacionRequestDTO, historialCotizacion);
        return historialCotizacion;
    }

    public void updateEntityFromDTO(HistorialCotizacionRequestDTO historialCotizacionRequestDTO,
                                    HistorialCotizacion historialCotizacion) {
        if (historialCotizacionRequestDTO.getObservacion() != null) {
            historialCotizacion.setObservacion(historialCotizacionRequestDTO.getObservacion());
        }
    }

    public HistorialCotizacionResponseDTO toResponseDTO(HistorialCotizacion historialCotizacion) {
        HistorialCotizacionResponseDTO historialCotizacionResponseDTO = new HistorialCotizacionResponseDTO();

        historialCotizacionResponseDTO.setId(historialCotizacion.getId());
        historialCotizacionResponseDTO.setUuid(historialCotizacion.getUuid());
        historialCotizacionResponseDTO.setObservacion(historialCotizacion.getObservacion());
        historialCotizacionResponseDTO.setFechaCreacion(historialCotizacion.getFechaCreacion());

        if (historialCotizacion.getUsuario() != null) {
            historialCotizacionResponseDTO.setUsuarioId(historialCotizacion.getUsuario().getId());
            historialCotizacionResponseDTO.setUsuarioNombre(historialCotizacion.getUsuario().getNombre());
            historialCotizacionResponseDTO.setUsuarioEmail(historialCotizacion.getUsuario().getEmail());
        }

        if (historialCotizacion.getCotizacion() != null) {
            historialCotizacionResponseDTO.setCotizacionId(historialCotizacion.getCotizacion().getId());
            historialCotizacionResponseDTO.setCodigoCotizacion(historialCotizacion.getCotizacion().getCodigoCotizacion());
        }

        if (historialCotizacion.getEstadoCotizacion() != null) {
            historialCotizacionResponseDTO.setEstadoCotizacionId(historialCotizacion.getEstadoCotizacion().getId());
            historialCotizacionResponseDTO
                    .setEstadoCotizacionDescripcion(historialCotizacion.getEstadoCotizacion().getDescripcion());
        }

        return historialCotizacionResponseDTO;
    }

    public HistorialCotizacionSimpleDTO toSimpleDTO(HistorialCotizacion historialCotizacion) {
        HistorialCotizacionSimpleDTO historialCotizacionSimpleDTO = new HistorialCotizacionSimpleDTO();

        historialCotizacionSimpleDTO.setId(historialCotizacion.getId());
        historialCotizacionSimpleDTO.setUuid(historialCotizacion.getUuid());
        historialCotizacionSimpleDTO.setObservacion(historialCotizacion.getObservacion());
        historialCotizacionSimpleDTO.setFechaCreacion(historialCotizacion.getFechaCreacion());

        if (historialCotizacion.getUsuario() != null) {
            historialCotizacionSimpleDTO.setUsuarioId(historialCotizacion.getUsuario().getId());
            historialCotizacionSimpleDTO.setUsuarioNombre(historialCotizacion.getUsuario().getNombre());
            historialCotizacionSimpleDTO.setUsuarioEmail(historialCotizacion.getUsuario().getEmail());
        }

        if (historialCotizacion.getEstadoCotizacion() != null) {
            historialCotizacionSimpleDTO.setEstadoCotizacionId(historialCotizacion.getEstadoCotizacion().getId());
            historialCotizacionSimpleDTO
                    .setEstadoCotizacionDescripcion(historialCotizacion.getEstadoCotizacion().getDescripcion());
        }

        return historialCotizacionSimpleDTO;
    }
}