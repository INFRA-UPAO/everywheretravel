package com.everywhere.backend.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleCotizacionRequestDto {
    private Integer cantidad;
    private Integer unidad;
    private String descripcion; 
    private BigDecimal comision;
    private BigDecimal precioHistorico;
    private Boolean seleccionado;

    private Integer categoriaId;
    private Integer productoId;
    private Integer proveedorId;
    private Integer operadorId;
}