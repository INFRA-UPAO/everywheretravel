package com.everywhere.backend.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DetalleLiquidacionSinLiquidacionDTO {

    private Integer id;
    private String ticket;
    private String documentoCobro;
    private BigDecimal costoTicket;
    private BigDecimal cargoServicio;
    private BigDecimal valorVenta;
    private String feeEmision;
    private String documentoFee;
    private String comision;
    private String facturaCompra;
    private String boletaPasajero;
    private BigDecimal montoDescuento;
    private BigDecimal pagoPaxUSD;
    private BigDecimal pagoPaxPEN;
    private LocalDateTime creado;
    private LocalDateTime actualizado;

    // Solo las relaciones del detalle (SIN la liquidación)
    private ViajeroConPersonaNaturalDTO viajero;
    private ProductoResponseDTO producto;
    private ProveedorResponseDTO proveedor;
    private OperadorResponseDTO operador;
}