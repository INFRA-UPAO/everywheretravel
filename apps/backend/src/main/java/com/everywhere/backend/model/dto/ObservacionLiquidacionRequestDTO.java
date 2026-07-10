package com.everywhere.backend.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ObservacionLiquidacionRequestDTO {

    private String descripcion;
    private BigDecimal valor;
    private String documento;
    private String numeroDocumento;
    private Integer liquidacionId;
}
