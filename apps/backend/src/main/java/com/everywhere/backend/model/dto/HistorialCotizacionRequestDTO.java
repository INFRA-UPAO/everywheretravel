package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class HistorialCotizacionRequestDTO {
    private String observacion;
    private Integer usuarioId;
    private Integer cotizacionId;
    private Integer estadoCotizacionId;
}