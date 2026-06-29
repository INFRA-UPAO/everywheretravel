package com.everywhere.backend.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReciboUpdateDTO {
    private LocalDate fechaEmision;
    private String fileVenta;
    private String observaciones;
    private LocalDate fechaVencimiento;
    private Integer detalleDocumentoId;
    private Integer sucursalId;
    private Integer personaJuridicaId;
    private Integer formaPagoId;
}
