package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class HistorialCotizacionSimpleDTO {
    private Integer id;
    private UUID uuid;
    private String observacion;
    private LocalDateTime fechaCreacion;

    private Integer usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;

    private Integer estadoCotizacionId;
    private String estadoCotizacionDescripcion;
}