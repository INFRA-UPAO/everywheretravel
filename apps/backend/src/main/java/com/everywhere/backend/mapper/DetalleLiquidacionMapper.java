package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionSinLiquidacionDTO;
import com.everywhere.backend.model.entity.DetalleLiquidacion;

import jakarta.annotation.PostConstruct;

import org.modelmapper.ModelMapper; 
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DetalleLiquidacionMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void init() {
        modelMapper.typeMap(DetalleLiquidacionRequestDTO.class, DetalleLiquidacion.class)
                .addMappings(mapper -> mapper.skip(DetalleLiquidacion::setId));
    }

    public DetalleLiquidacionResponseDTO toResponseDTO(DetalleLiquidacion detalleLiquidacion) {
        DetalleLiquidacionResponseDTO detalleLiquidacionResponseDTO = modelMapper.map(detalleLiquidacion, DetalleLiquidacionResponseDTO.class);
        return detalleLiquidacionResponseDTO;
    }

    public DetalleLiquidacionSinLiquidacionDTO toSinLiquidacionDTO(DetalleLiquidacion detalleLiquidacion) {
        DetalleLiquidacionSinLiquidacionDTO detalleLiquidacionSinLiquidacionDTO = modelMapper.map(detalleLiquidacion, DetalleLiquidacionSinLiquidacionDTO.class);
        return detalleLiquidacionSinLiquidacionDTO;
    }

    public DetalleLiquidacion toEntity(DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacion detalleLiquidacion = new DetalleLiquidacion();
        updateEntityFromDTO(detalleLiquidacionRequestDTO, detalleLiquidacion);
        return detalleLiquidacion;
    }

    public void updateEntityFromDTO(DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO, DetalleLiquidacion detalleLiquidacion) {
        if (detalleLiquidacionRequestDTO.getTicket() != null) {
            detalleLiquidacion.setTicket(detalleLiquidacionRequestDTO.getTicket());
        }
        if (detalleLiquidacionRequestDTO.getDocumentoCobro() != null) {
            detalleLiquidacion.setDocumentoCobro(detalleLiquidacionRequestDTO.getDocumentoCobro());
        }
        if (detalleLiquidacionRequestDTO.getCostoTicket() != null) {
            detalleLiquidacion.setCostoTicket(detalleLiquidacionRequestDTO.getCostoTicket());
        }
        if (detalleLiquidacionRequestDTO.getCargoServicio() != null) {
            detalleLiquidacion.setCargoServicio(detalleLiquidacionRequestDTO.getCargoServicio());
        }
        if (detalleLiquidacionRequestDTO.getValorVenta() != null) {
            detalleLiquidacion.setValorVenta(detalleLiquidacionRequestDTO.getValorVenta());
        }
        if (detalleLiquidacionRequestDTO.getFeeEmision() != null) {
            detalleLiquidacion.setFeeEmision(detalleLiquidacionRequestDTO.getFeeEmision());
        }
        if (detalleLiquidacionRequestDTO.getDocumentoFee() != null) {
            detalleLiquidacion.setDocumentoFee(detalleLiquidacionRequestDTO.getDocumentoFee());
        }
        if (detalleLiquidacionRequestDTO.getComision() != null) {
            detalleLiquidacion.setComision(detalleLiquidacionRequestDTO.getComision());
        }
        if (detalleLiquidacionRequestDTO.getFacturaCompra() != null) {
            detalleLiquidacion.setFacturaCompra(detalleLiquidacionRequestDTO.getFacturaCompra());
        }
        if (detalleLiquidacionRequestDTO.getBoletaPasajero() != null) {
            detalleLiquidacion.setBoletaPasajero(detalleLiquidacionRequestDTO.getBoletaPasajero());
        }
        if (detalleLiquidacionRequestDTO.getMontoDescuento() != null) {
            detalleLiquidacion.setMontoDescuento(detalleLiquidacionRequestDTO.getMontoDescuento());
        }
        if (detalleLiquidacionRequestDTO.getPagoPaxUSD() != null) {
            detalleLiquidacion.setPagoPaxUSD(detalleLiquidacionRequestDTO.getPagoPaxUSD());
        }
        if (detalleLiquidacionRequestDTO.getPagoPaxPEN() != null) {
            detalleLiquidacion.setPagoPaxPEN(detalleLiquidacionRequestDTO.getPagoPaxPEN());
        }
    }
}