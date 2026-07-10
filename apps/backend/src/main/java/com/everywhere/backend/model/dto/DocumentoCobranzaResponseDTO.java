package com.everywhere.backend.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentoCobranzaResponseDTO {

    private Long id;
    private String serie;
    private Integer correlativo;
    private LocalDate fechaEmision;
    private String observaciones;
    private String fileVenta;
    private BigDecimal costoEnvio;
    private String moneda;

    private Integer cotizacionId;
    private String codigoCotizacion;
    private Integer personaId;
    private Integer sucursalId;
    private Integer formaPagoId;

    private String clienteNombre;
    private String clienteDocumento;
    private String tipoDocumentoCliente;
    private String sucursalDescripcion;
    private String formaPagoDescripcion;

    private Integer personaJuridicaId;
    private String personaJuridicaRuc;
    private String personaJuridicaRazonSocial;

    private Integer detalleDocumentoId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<DetalleDocumentoCobranzaResponseDTO> detalles;
}