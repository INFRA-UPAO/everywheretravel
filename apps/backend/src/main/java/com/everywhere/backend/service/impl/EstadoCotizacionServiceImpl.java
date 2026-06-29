package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ConflictException;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.EstadoCotizacionMapper;
import com.everywhere.backend.model.dto.EstadoCotizacionRequestDTO;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDTO;
import com.everywhere.backend.model.entity.EstadoCotizacion;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.repository.EstadoCotizacionRepository;
import com.everywhere.backend.service.EstadoCotizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoCotizacionServiceImpl implements EstadoCotizacionService {

    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final EstadoCotizacionMapper estadoCotizacionMapper;
    private final CotizacionRepository cotizacionRepository;

    @Override
    public EstadoCotizacionResponseDTO create(EstadoCotizacionRequestDTO estadoCotizacionRequestDTO) {
        EstadoCotizacion estadoCotizacion = estadoCotizacionMapper.toEntity(estadoCotizacionRequestDTO); 
        return estadoCotizacionMapper.toResponseDTO(estadoCotizacionRepository.save(estadoCotizacion));
    }

    @Override
    public EstadoCotizacionResponseDTO update(Integer id, EstadoCotizacionRequestDTO estadoCotizacionRequestDTO) {
        if (!estadoCotizacionRepository.existsById(id))
            throw new ResourceNotFoundException("Estado de Cotización no encontrado con ID: " + id);

        EstadoCotizacion existing = estadoCotizacionRepository.findById(id).get();
        estadoCotizacionMapper.updateEntityFromDTO(estadoCotizacionRequestDTO, existing); 
        return estadoCotizacionMapper.toResponseDTO(estadoCotizacionRepository.save(existing));
    }

    @Override
    public EstadoCotizacionResponseDTO getById(Integer id) {
        return estadoCotizacionRepository.findById(id).map(estadoCotizacionMapper::toResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Estado de Cotización no encontrado con ID: " + id));
    }

    @Override
    public List<EstadoCotizacionResponseDTO> getAll() {
        return mapToResponseList(estadoCotizacionRepository.findAll());
    }

    @Override
    public void delete(Integer ida) {
        if (!estadoCotizacionRepository.existsById(ida))
            throw new ResourceNotFoundException("Estado de Cotización no encontrado con ID: " + ida);

        // Validar que no existan cotizaciones vinculadas a este estado
        Long cotizacionesCount = cotizacionRepository.countByEstadoCotizacionId(ida);
        if (cotizacionesCount > 0) {
            throw new ConflictException(
                    "No se puede eliminar el Estado de Cotización porque hay " + cotizacionesCount +
                            " cotización(es) vinculada(s).",
                    "/api/v1/estados-cotizacion/" + ida
            );
        }

        estadoCotizacionRepository.deleteById(ida);
    }

    private List<EstadoCotizacionResponseDTO> mapToResponseList(List<EstadoCotizacion> estadosCotizacion) {
        return estadosCotizacion.stream().map(estadoCotizacionMapper::toResponseDTO).toList();
    }
}