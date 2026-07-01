package com.everywhere.backend.model.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class DetalleReciboRequestDTO {
    
    @PositiveOrZero(message = "La cantidad debe ser positiva")
    private Integer cantidad;

    private String descripcion;
    
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio;

    private Integer reciboId;
    private Integer productoId;
}