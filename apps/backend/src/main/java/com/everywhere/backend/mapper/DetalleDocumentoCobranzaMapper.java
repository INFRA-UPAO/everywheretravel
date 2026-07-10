package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleCotizacionSimpleDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaRequestDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.Producto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DetalleDocumentoCobranzaMapper {

    private final ModelMapper modelMapper;

    public DetalleDocumentoCobranza toEntity(DetalleDocumentoCobranzaRequestDTO detalleDocumentoCobranzaRequestDTO) {
        return modelMapper.map(detalleDocumentoCobranzaRequestDTO, DetalleDocumentoCobranza.class);
    }

    public DetalleDocumentoCobranzaResponseDTO toResponseDTO(DetalleDocumentoCobranza detalleDocumentoCobranza) {
         DetalleDocumentoCobranzaResponseDTO dto = modelMapper.map(detalleDocumentoCobranza, DetalleDocumentoCobranzaResponseDTO.class);
         
        if (detalleDocumentoCobranza.getProducto() != null) {
            dto.setProductoId(detalleDocumentoCobranza.getProducto().getId());
            dto.setProductoDescripcion(detalleDocumentoCobranza.getProducto().getTipo());
        } 
        if (detalleDocumentoCobranza.getDocumentoCobranza() != null) {
            dto.setDocumentoCobranzaId(detalleDocumentoCobranza.getDocumentoCobranza().getId());
            dto.setDocumentoCobranzaNumero(
                String.format("%s-%09d", 
                    detalleDocumentoCobranza.getDocumentoCobranza().getSerie(),
                    detalleDocumentoCobranza.getDocumentoCobranza().getCorrelativo()));
        }
        
        return dto;
    }

    public void updateEntityFromRequest(DetalleDocumentoCobranza detalleDocumentoCobranza, DetalleDocumentoCobranzaRequestDTO detalleDocumentoCobranzaRequestDTO) {
        modelMapper.map(detalleDocumentoCobranzaRequestDTO, detalleDocumentoCobranza);
    }

    //Convierte los detalles seleccionados de una cotización a detalles de documento de cobranza
    public List<DetalleDocumentoCobranza> fromCotizacionDetalles(List<DetalleCotizacionSimpleDTO> detallesCotizacion, DocumentoCobranza documentoCobranza) {
        List<DetalleDocumentoCobranza> detalles = new ArrayList<>();

        if (detallesCotizacion != null) {
            for (DetalleCotizacionSimpleDTO detalleCotizacionSimpleDTO : detallesCotizacion) {
                if (detalleCotizacionSimpleDTO.getSeleccionado() != null && detalleCotizacionSimpleDTO.getSeleccionado()) {
                    DetalleDocumentoCobranza detalleDocumentoCobranza = new DetalleDocumentoCobranza();
                    detalleDocumentoCobranza.setDocumentoCobranza(documentoCobranza);
                    detalleDocumentoCobranza.setCantidad(detalleCotizacionSimpleDTO.getCantidad() != null ? detalleCotizacionSimpleDTO.getCantidad() : 0);
                    detalleDocumentoCobranza.setDescripcion(detalleCotizacionSimpleDTO.getDescripcion());
                    detalleDocumentoCobranza.setPrecio(detalleCotizacionSimpleDTO.getPrecioHistorico() != null ? detalleCotizacionSimpleDTO.getPrecioHistorico() : BigDecimal.ZERO);

                    if (detalleCotizacionSimpleDTO.getProducto() != null) {
                        Producto producto = new Producto();
                        producto.setId(detalleCotizacionSimpleDTO.getProducto().getId());
                        detalleDocumentoCobranza.setProducto(producto);
                    }
                    detalles.add(detalleDocumentoCobranza);
                }
            }
        }
        return detalles;
    }
}