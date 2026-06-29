package com.everywhere.backend.util.pdf;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;

/**
 * Generador de PDF para DocumentoCobranza - Extiende PdfGenerator
 */
@Component
public class DocumentoCobranzaPdfGenerator extends PdfGenerator<DocumentoCobranzaResponseDTO, DetalleDocumentoCobranzaResponseDTO> {

    public DocumentoCobranzaPdfGenerator(NumberToTextConverter numberToTextConverter) {
        super(numberToTextConverter);
    }

    @Override
    protected String getDocumentTitle() {
        return "DOCUMENTO DE COBRANZA";
    }

    @Override
    protected String getFooterText() {
        return "Representación Impresa de DOCUMENTO DE COBRANZA";
    }

    @Override
    protected List<DetalleDocumentoCobranzaResponseDTO> getDetalles(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getDetalles();
    }

    @Override
    protected String getNumeroDocumento(DocumentoCobranzaResponseDTO documentoDTO) {
        // Concatenar serie y correlativo para formar el número completo (ej: DC01-000000001)
        if (documentoDTO.getSerie() != null && documentoDTO.getCorrelativo() != null) {
            return String.format("%s-%09d", documentoDTO.getSerie(), documentoDTO.getCorrelativo());
        }
        return null;
    }

    @Override
    protected String getFechaEmision(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getFechaEmision() != null 
            ? documentoDTO.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) 
            : null;
    }

    @Override
    protected String getClienteNombre(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getClienteNombre();
    }

    @Override
    protected String getClienteDocumento(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getClienteDocumento();
    }

    @Override
    protected String getTipoDocumentoCliente(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getTipoDocumentoCliente();
    }

    @Override
    protected String getSucursalDescripcion(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getSucursalDescripcion();
    }

    @Override
    protected String getMoneda(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getMoneda();
    }

    @Override
    protected String getFileVenta(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getFileVenta();
    }

    @Override
    protected String getFormaPagoDescripcion(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getFormaPagoDescripcion();
    }

    @Override
    protected String getObservaciones(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getObservaciones();
    }

    @Override
    protected BigDecimal getCostoEnvio(DocumentoCobranzaResponseDTO documentoDTO) {
        return documentoDTO.getCostoEnvio();
    }

    @Override
    protected Integer getCantidad(DetalleDocumentoCobranzaResponseDTO detalle) {
        return detalle.getCantidad();
    }

    @Override
    protected String getProductoDescripcion(DetalleDocumentoCobranzaResponseDTO detalle) {
        return detalle.getProductoDescripcion();
    }

    @Override
    protected String getDescripcionDetalle(DetalleDocumentoCobranzaResponseDTO detalle) {
        return detalle.getDescripcion();
    }

    @Override
    protected BigDecimal getPrecio(DetalleDocumentoCobranzaResponseDTO detalle) {
        return detalle.getPrecio();
    }

    @Override
    protected Long getDetalleId(DetalleDocumentoCobranzaResponseDTO detalle) {
        return detalle.getId();
    }
}
