package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ConflictException;
import com.everywhere.backend.mapper.FormaPagoMapper;
import com.everywhere.backend.model.dto.FormaPagoRequestDTO;
import com.everywhere.backend.model.dto.FormaPagoResponseDTO;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.service.FormaPagoService;

import lombok.RequiredArgsConstructor;

import com.everywhere.backend.exceptions.ResourceNotFoundException; 
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormaPagoServiceImpl implements FormaPagoService {

    private final FormaPagoRepository formaPagoRepository;
    private final FormaPagoMapper formaPagoMapper;
    private final CotizacionRepository cotizacionRepository;

    @Override
    public List<FormaPagoResponseDTO> findAll() {
        return mapToResponseList(formaPagoRepository.findAll());
    }

    @Override
    public FormaPagoResponseDTO findById(Integer id) {
        return formaPagoRepository.findById(id).map(formaPagoMapper::toResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id));
    }

    @Override
    public FormaPagoResponseDTO findByCodigo(Integer codigo) {
        return formaPagoRepository.findByCodigo(codigo).map(formaPagoMapper::toResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con código: " + codigo));
    }

    @Override
    public List<FormaPagoResponseDTO> findByDescripcion(String descripcion) {
        return mapToResponseList(formaPagoRepository.findByDescripcionContainingIgnoreCase(descripcion));
    }

    @Override
    public FormaPagoResponseDTO save(FormaPagoRequestDTO formaPagoRequestDTO) {
        FormaPago formaPago = formaPagoMapper.toEntity(formaPagoRequestDTO);
        return formaPagoMapper.toResponseDTO(formaPagoRepository.save(formaPago));
    }

    @Override
    public FormaPagoResponseDTO update(Integer id, FormaPagoRequestDTO formaPagoRequestDTO) {
        if (!formaPagoRepository.existsById(id))
            throw new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id);

        FormaPago formaPago = formaPagoRepository.findById(id).get();

        if (formaPagoRequestDTO.getCodigo() != null && 
            !formaPagoRequestDTO.getCodigo().equals(formaPago.getCodigo()) &&
            formaPagoRepository.existsByCodigo(formaPagoRequestDTO.getCodigo())) {
            throw new DataIntegrityViolationException("Ya existe una forma de pago con el código: " + formaPagoRequestDTO.getCodigo());
        }

        if (formaPagoRequestDTO.getCodigo() != null)
            formaPago.setCodigo(formaPagoRequestDTO.getCodigo());

        if (formaPagoRequestDTO.getDescripcion() != null && !formaPagoRequestDTO.getDescripcion().trim().isEmpty())
            formaPago.setDescripcion(formaPagoRequestDTO.getDescripcion());

        return formaPagoMapper.toResponseDTO(formaPagoRepository.save(formaPago));
    }

    @Override
    public void deleteById(Integer id) {
        if (!formaPagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id);
        }

        long cotizacionesCount = cotizacionRepository.countByFormaPagoId(id);
        if (cotizacionesCount > 0) {
            throw new ConflictException(
                    "No se puede eliminar esta forma de pago porque tiene " + cotizacionesCount + " cotización(es) asociada(s).",
                    "/api/v1/formas-pago/" + id
            );
        }

        formaPagoRepository.deleteById(id);
    }

    private List<FormaPagoResponseDTO> mapToResponseList(List<FormaPago> formasPago) {
        return formasPago.stream().map(formaPagoMapper::toResponseDTO).toList();
    }
}