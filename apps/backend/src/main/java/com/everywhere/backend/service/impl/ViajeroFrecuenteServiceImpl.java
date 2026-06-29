package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ViajeroFrecuenteMapper;
import com.everywhere.backend.model.dto.ViajeroFrecuenteRequestDto;
import com.everywhere.backend.model.dto.ViajeroFrecuenteResponseDto;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.model.entity.ViajeroFrecuente;
import com.everywhere.backend.repository.ViajeroFrecuenteRepository;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.service.ViajeroFrecuenteService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViajeroFrecuenteServiceImpl implements ViajeroFrecuenteService {

    private final ViajeroFrecuenteRepository viajeroFrecuenteRepository;
    private final ViajeroRepository viajeroRepository;
    private final ViajeroFrecuenteMapper viajeroFrecuenteMapper;

    @Override
    public List<ViajeroFrecuenteResponseDto> findAll() {
        return mapToResponseList(viajeroFrecuenteRepository.findAll());
    }

    @Override
    public ViajeroFrecuenteResponseDto crear(Integer viajeroId, ViajeroFrecuenteRequestDto viajeroFrecuenteRequestDto) {
        if (viajeroId == null) throw new IllegalArgumentException("El ID del viajero no puede ser nulo");

        if (!viajeroRepository.existsById(viajeroId))
            throw new ResourceNotFoundException("Viajero no encontrado con id: " + viajeroId);

        Viajero viajero = viajeroRepository.findById(viajeroId).get();
        ViajeroFrecuente viajeroFrecuente = viajeroFrecuenteMapper.toEntity(viajeroFrecuenteRequestDto); 
        viajeroFrecuente.setViajero(viajero);
        return viajeroFrecuenteMapper.toResponse(viajeroFrecuenteRepository.save(viajeroFrecuente));
    }

    @Override
    public ViajeroFrecuenteResponseDto buscarPorId(Integer id) {
        ViajeroFrecuente viajeroFrecuente = viajeroFrecuenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ViajeroFrecuente no encontrado con id: " + id));

        return viajeroFrecuenteMapper.toResponse(viajeroFrecuente);
    }

    @Override
    public List<ViajeroFrecuenteResponseDto> listarPorViajero(Integer viajeroId) {
        return mapToResponseList(viajeroFrecuenteRepository.findByViajero_Id(viajeroId));
    }


    @Override
    public void eliminar(Integer id) {
        if (!viajeroFrecuenteRepository.existsById(id)) throw new ResourceNotFoundException("ViajeroFrecuente no encontrado con id: " + id);
        viajeroFrecuenteRepository.deleteById(id);
    }

    @Override
    public ViajeroFrecuenteResponseDto actualizar(Integer id, ViajeroFrecuenteRequestDto viajeroFrecuenteRequestDto) {
        if (!viajeroFrecuenteRepository.existsById(id))
            throw new ResourceNotFoundException("ViajeroFrecuente no encontrado con id: " + id);

        ViajeroFrecuente viajeroFrecuente = viajeroFrecuenteRepository.findById(id).get();
        
        if (viajeroFrecuenteRepository.existsByAreolineaAndCodigo(
                viajeroFrecuenteRequestDto.getAreolinea(),
                viajeroFrecuenteRequestDto.getCodigo())) {
            throw new IllegalArgumentException(
                    "Ya existe un viajero frecuente con la aerolínea " +
                            viajeroFrecuenteRequestDto.getAreolinea() +
                            " y el código " + viajeroFrecuenteRequestDto.getCodigo()
            );
        }

        viajeroFrecuenteMapper.updateEntityFromDto(viajeroFrecuenteRequestDto, viajeroFrecuente);
        return viajeroFrecuenteMapper.toResponse(viajeroFrecuenteRepository.save(viajeroFrecuente));
    }

    @Override
    public List<ViajeroFrecuenteResponseDto> buscarPorViajeroId(Integer viajeroId) {
        return mapToResponseList(viajeroFrecuenteRepository.findByViajero_Id(viajeroId));
    }

    private List<ViajeroFrecuenteResponseDto> mapToResponseList(List<ViajeroFrecuente> viajerosFrecuentes) {
        return viajerosFrecuentes.stream().map(viajeroFrecuenteMapper::toResponse).toList();
    }
}