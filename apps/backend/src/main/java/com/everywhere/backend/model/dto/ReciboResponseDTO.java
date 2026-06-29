package com.everywhere.backend.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ReciboResponseDTO {
    private Integer id;
    private String serie;
    private Integer correlativo;
    private LocalDate fechaEmision;
    private String observaciones;
    private LocalDate fechaVencimiento;
    private String fileVenta;
    private String moneda;

    private Integer cotizacionId;
    private String codigoCotizacion;
    private Integer personaId;
    private Integer sucursalId;
    private Integer formaPagoId;
    private Integer detalleDocumentoId;

    private String clienteNombre;
    private String clienteDocumento;
    private String tipoDocumentoCliente;
    private String sucursalDescripcion;
    private String formaPagoDescripcion;

    private Integer personaJuridicaId;
    private String personaJuridicaRuc;
    private String personaJuridicaRazonSocial;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<DetalleReciboResponseDTO> detalles;
}
