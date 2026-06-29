package com.everywhere.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagoPaxResponseDTO {
    private Integer id;
    private BigDecimal monto;
    private String moneda;
    private String detalle;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private LiquidacionResponseDTO liquidacion;
    private FormaPagoResponseDTO formaPago;
}
