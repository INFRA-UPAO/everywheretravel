package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleCotizacionSimpleDTO;
import com.everywhere.backend.model.dto.DetalleReciboRequestDTO;
import com.everywhere.backend.model.dto.DetalleReciboResponseDTO;
import com.everywhere.backend.model.entity.DetalleRecibo;
import com.everywhere.backend.model.entity.Recibo;
import com.everywhere.backend.model.entity.Producto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DetalleReciboMapper {
    
    private final ModelMapper modelMapper;

    public DetalleRecibo toEntity(DetalleReciboRequestDTO detalleReciboRequestDTO) {
        return modelMapper.map(detalleReciboRequestDTO, DetalleRecibo.class);
    }

    public DetalleReciboResponseDTO toResponseDTO(DetalleRecibo detalleRecibo) {
        DetalleReciboResponseDTO dto = modelMapper.map(detalleRecibo, DetalleReciboResponseDTO.class);
        
        if (detalleRecibo.getProducto() != null) {
            dto.setProductoId(detalleRecibo.getProducto().getId());
            dto.setProductoDescripcion(detalleRecibo.getProducto().getTipo());
        }
        if (detalleRecibo.getRecibo() != null) {
            dto.setReciboId(detalleRecibo.getRecibo().getId());
            dto.setReciboNumero(
                String.format("%s-%09d", 
                    detalleRecibo.getRecibo().getSerie(),
                    detalleRecibo.getRecibo().getCorrelativo()));
        }
        
        return dto;
    }

    public void updateEntityFromRequest(DetalleRecibo detalleRecibo, DetalleReciboRequestDTO detalleReciboRequestDTO) {
        modelMapper.map(detalleReciboRequestDTO, detalleRecibo);
    }

    // Convierte los detalles seleccionados de una cotización a detalles de recibo
    public List<DetalleRecibo> fromCotizacionDetalles(List<DetalleCotizacionSimpleDTO> detallesCotizacion, Recibo recibo) {
        List<DetalleRecibo> detalles = new ArrayList<>();

        if (detallesCotizacion != null) {
            for (DetalleCotizacionSimpleDTO detalleCotizacionSimpleDTO : detallesCotizacion) {
                if (detalleCotizacionSimpleDTO.getSeleccionado() != null && detalleCotizacionSimpleDTO.getSeleccionado()) {
                    DetalleRecibo detalleRecibo = new DetalleRecibo();
                    detalleRecibo.setRecibo(recibo);
                    detalleRecibo.setCantidad(detalleCotizacionSimpleDTO.getCantidad() != null ? detalleCotizacionSimpleDTO.getCantidad() : 0);
                    detalleRecibo.setDescripcion(detalleCotizacionSimpleDTO.getDescripcion());
                    detalleRecibo.setPrecio(detalleCotizacionSimpleDTO.getPrecioHistorico() != null ? detalleCotizacionSimpleDTO.getPrecioHistorico() : BigDecimal.ZERO);

                    if (detalleCotizacionSimpleDTO.getProducto() != null) {
                        Producto producto = new Producto();
                        producto.setId(detalleCotizacionSimpleDTO.getProducto().getId());
                        detalleRecibo.setProducto(producto);
                    }
                    detalles.add(detalleRecibo);
                }
            }
        }
        return detalles;
    }
}
