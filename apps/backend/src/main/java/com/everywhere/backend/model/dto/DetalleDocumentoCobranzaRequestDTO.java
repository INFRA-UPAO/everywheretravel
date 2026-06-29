package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleDocumentoCobranzaRequestDTO {
    
    @PositiveOrZero(message = "La cantidad debe ser positiva")
    private Integer cantidad;
    
    private String descripcion;
    
    @PositiveOrZero(message = "El precio debe ser positivo")
    private BigDecimal precio;
    
    private Long documentoCobranzaId;
    private Integer productoId;
}