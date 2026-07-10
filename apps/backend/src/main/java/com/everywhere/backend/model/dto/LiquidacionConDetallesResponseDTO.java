package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LiquidacionConDetallesResponseDTO {

    private Integer id;
    private String numero;
    private LocalDate fechaCompra; 
    private String destino;
    private Integer numeroPasajeros; 
    private LocalDateTime creado;
    private LocalDateTime actualizado;

    // Relaciones de la liquidación
    private ProductoResponseDTO producto;
    private FormaPagoResponseDTO formaPago;

    // Lista de detalles anidados (SIN la liquidación repetida)
    private List<DetalleLiquidacionSimpleDTO> detalles;

    // Lista de observaciones anidadas (SIN la liquidación repetida)
    private List<ObservacionLiquidacionSimpleDTO> observaciones;
}
