package com.everywhere.backend.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ObservacionLiquidacionSimpleDTO {

    private Long id;
    private String descripcion;
    private BigDecimal valor;
    private String documento;
    private String numeroDocumento;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    // NO incluye la liquidación para evitar referencia circular
}
