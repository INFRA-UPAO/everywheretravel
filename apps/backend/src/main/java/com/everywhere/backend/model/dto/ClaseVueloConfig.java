package com.everywhere.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaseVueloConfig {
    private Integer detalleId; // ID del DetalleCotizacion
    private String clase; // "Económica", "Ejecutiva", "Primera"
}
