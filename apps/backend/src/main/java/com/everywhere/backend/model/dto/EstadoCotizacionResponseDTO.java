package com.everywhere.backend.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EstadoCotizacionResponseDTO {
    private int id;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
