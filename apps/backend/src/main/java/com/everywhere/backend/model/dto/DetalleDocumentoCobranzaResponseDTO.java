package com.everywhere.backend.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DetalleDocumentoCobranzaResponseDTO {
    
    private Long id;
    private Integer cantidad;
    private String descripcion;
    private BigDecimal precio;
    private LocalDateTime fechaCreacion;
    
    private Long documentoCobranzaId;
    private String documentoCobranzaNumero;
    
    private Integer productoId;
    private String productoDescripcion;
}