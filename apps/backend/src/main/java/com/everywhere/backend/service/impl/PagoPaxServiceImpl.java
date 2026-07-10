package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.PagoPaxMapper;
import com.everywhere.backend.model.dto.PagoPaxRequestDTO;
import com.everywhere.backend.model.dto.PagoPaxResponseDTO;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.PagoPax;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.repository.LiquidacionRepository;
import com.everywhere.backend.repository.PagoPaxRepository;
import com.everywhere.backend.service.PagoPaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PagoPaxServiceImpl implements PagoPaxService {

    private final PagoPaxRepository pagoPaxRepository;
    private final LiquidacionRepository liquidacionRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final PagoPaxMapper pagoPaxMapper;

    @Override
    @Transactional
    public PagoPaxResponseDTO create(PagoPaxRequestDTO requestDTO) {
        // Validar que existe la liquidación
        Liquidacion liquidacion = liquidacionRepository.findById(requestDTO.getLiquidacionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Liquidación no encontrada con ID: " + requestDTO.getLiquidacionId()));

        // Validar que existe la forma de pago
        FormaPago formaPago = formaPagoRepository.findById(requestDTO.getFormaPagoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Forma de pago no encontrada con ID: " + requestDTO.getFormaPagoId()));

        // Crear la entidad
        PagoPax pagoPax = pagoPaxMapper.toEntity(requestDTO);
        pagoPax.setLiquidacion(liquidacion);
        pagoPax.setFormaPago(formaPago);

        // Guardar
        pagoPax = pagoPaxRepository.save(pagoPax);

        return pagoPaxMapper.toResponseDTO(pagoPax);
    }

    @Override
    public PagoPaxResponseDTO findById(Integer id) {
        PagoPax pagoPax = pagoPaxRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago Pax no encontrado con ID: " + id));

        return pagoPaxMapper.toResponseDTO(pagoPax);
    }

    @Override
    public List<PagoPaxResponseDTO> findAll() {
        return pagoPaxRepository.findAllWithRelations().stream()
                .map(pagoPaxMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagoPaxResponseDTO> findByLiquidacionId(Integer liquidacionId) {
        // Validar que existe la liquidación
        if (!liquidacionRepository.existsById(liquidacionId)) {
            throw new ResourceNotFoundException("Liquidación no encontrada con ID: " + liquidacionId);
        }

        return pagoPaxRepository.findByLiquidacionId(liquidacionId).stream()
                .map(pagoPaxMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PagoPaxResponseDTO update(Integer id, PagoPaxRequestDTO requestDTO) {
        // Buscar el pago pax existente
        PagoPax pagoPax = pagoPaxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago Pax no encontrado con ID: " + id));

        // Actualizar datos básicos
        pagoPaxMapper.updateEntityFromRequestDTO(pagoPax, requestDTO);

        // Actualizar liquidación si cambió
        if (requestDTO.getLiquidacionId() != null 
            && !requestDTO.getLiquidacionId().equals(pagoPax.getLiquidacion().getId())) {
            Liquidacion liquidacion = liquidacionRepository.findById(requestDTO.getLiquidacionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Liquidación no encontrada con ID: " + requestDTO.getLiquidacionId()));
            pagoPax.setLiquidacion(liquidacion);
        }

        // Actualizar forma de pago si cambió
        if (requestDTO.getFormaPagoId() != null 
            && !requestDTO.getFormaPagoId().equals(pagoPax.getFormaPago().getId())) {
            FormaPago formaPago = formaPagoRepository.findById(requestDTO.getFormaPagoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Forma de pago no encontrada con ID: " + requestDTO.getFormaPagoId()));
            pagoPax.setFormaPago(formaPago);
        }

        // Guardar
        pagoPax = pagoPaxRepository.save(pagoPax);

        return pagoPaxMapper.toResponseDTO(pagoPax);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!pagoPaxRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pago Pax no encontrado con ID: " + id);
        }

        pagoPaxRepository.deleteById(id);
    }
}
