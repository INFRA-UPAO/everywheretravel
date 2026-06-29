package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DocumentoCobranzaUpdateDTO {
    private LocalDate fechaEmision;
    private String fileVenta;
    private Double costoEnvio;
    private String observaciones;
    private Integer detalleDocumentoId;
    private Integer sucursalId;
    private Integer personaJuridicaId;
    private Integer formaPagoId;
}