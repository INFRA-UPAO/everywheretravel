package com.everywhere.backend.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DetalleReciboResponseDTO {
    private Long id;
    private Integer cantidad;
    private String descripcion;
    private BigDecimal precio;
    private Integer productoId;
    private String productoDescripcion;
    private Integer reciboId;
    private String reciboNumero;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
