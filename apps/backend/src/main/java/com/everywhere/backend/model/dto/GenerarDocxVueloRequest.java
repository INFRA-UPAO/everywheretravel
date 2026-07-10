package com.everywhere.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerarDocxVueloRequest {
    private Integer cotizacionId;
    private List<ClaseVueloConfig> configuracionVuelos;
}
