package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LiquidacionRequestDTO {
    private String numero;
    private LocalDate fechaCompra; 
    private String destino;
    private Integer numeroPasajeros; 

    private Integer cotizacionId;
    private Integer productoId;
    private Integer formaPagoId;
    private Integer carpetaId;
}