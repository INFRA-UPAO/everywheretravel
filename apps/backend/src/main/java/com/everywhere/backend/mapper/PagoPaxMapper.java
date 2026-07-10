package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.PagoPaxRequestDTO;
import com.everywhere.backend.model.dto.PagoPaxResponseDTO;
import com.everywhere.backend.model.entity.PagoPax;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagoPaxMapper {

    private final ModelMapper modelMapper;
    private final LiquidacionMapper liquidacionMapper;
    private final FormaPagoMapper formaPagoMapper;

    /**
     * Convierte una entidad PagoPax a PagoPaxResponseDTO
     */
    public PagoPaxResponseDTO toResponseDTO(PagoPax pagoPax) {
        if (pagoPax == null) {
            return null;
        }

        PagoPaxResponseDTO dto = modelMapper.map(pagoPax, PagoPaxResponseDTO.class);

        // Mapear relaciones si existen
        if (pagoPax.getLiquidacion() != null) {
            dto.setLiquidacion(liquidacionMapper.toResponseDTO(pagoPax.getLiquidacion()));
        }

        if (pagoPax.getFormaPago() != null) {
            dto.setFormaPago(formaPagoMapper.toResponseDTO(pagoPax.getFormaPago()));
        }

        return dto;
    }

    /**
     * Convierte un PagoPaxRequestDTO a entidad PagoPax
     */
    public PagoPax toEntity(PagoPaxRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        PagoPax pagoPax = new PagoPax();
        pagoPax.setMonto(requestDTO.getMonto());
        pagoPax.setMoneda(requestDTO.getMoneda());
        pagoPax.setDetalle(requestDTO.getDetalle());

        return pagoPax;
    }

    /**
     * Actualiza una entidad PagoPax existente con datos del RequestDTO
     */
    public void updateEntityFromRequestDTO(PagoPax pagoPax, PagoPaxRequestDTO requestDTO) {
        if (pagoPax == null || requestDTO == null) {
            return;
        }

        if (requestDTO.getMonto() != null) {
            pagoPax.setMonto(requestDTO.getMonto());
        }
        if (requestDTO.getMoneda() != null) {
            pagoPax.setMoneda(requestDTO.getMoneda());
        }
        if (requestDTO.getDetalle() != null) {
            pagoPax.setDetalle(requestDTO.getDetalle());
        }
    }
}
