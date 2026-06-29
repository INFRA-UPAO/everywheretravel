package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.OperadorMapper;
import com.everywhere.backend.model.dto.OperadorRequestDTO;
import com.everywhere.backend.model.dto.OperadorResponseDTO;
import com.everywhere.backend.model.entity.Operador;
import com.everywhere.backend.repository.OperadorRepository;
import com.everywhere.backend.service.OperadorService;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository operadorRepository;
    private final OperadorMapper operadorMapper;

    @Override
    public List<OperadorResponseDTO> findAll() {
        return mapToResponseList(operadorRepository.findAll());
    }

    @Override
    public OperadorResponseDTO findById(int id) {
        Operador operador = operadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operador no encontrado con ID: " + id));
        return operadorMapper.toResponseDTO(operador);
    }

    @Override
    public OperadorResponseDTO findByNombre(String nombre) {
        Operador operador = operadorRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Operador no encontrado con nombre: " + nombre));

        return operadorMapper.toResponseDTO(operador);
    }

    @Override
    public OperadorResponseDTO save(OperadorRequestDTO operadorRequestDTO) {
        if (operadorRepository.existsByNombreIgnoreCase(operadorRequestDTO.getNombre()))
            throw new DataIntegrityViolationException("Ya existe un operador con el nombre: " + operadorRequestDTO.getNombre());
        Operador operador = operadorMapper.toEntity(operadorRequestDTO);
        return operadorMapper.toResponseDTO(operadorRepository.save(operador));
    }

    @Override
    public OperadorResponseDTO update(int id, OperadorRequestDTO operadorRequestDTO) {
        if (!operadorRepository.existsById(id))
            throw new ResourceNotFoundException("Operador con id " + id + " no encontrado");

        Operador operador = operadorRepository.findById(id).get();

        if (operadorRequestDTO.getNombre() != null && 
            !operadorRequestDTO.getNombre().equalsIgnoreCase(operador.getNombre()) &&
            operadorRepository.existsByNombreIgnoreCase(operadorRequestDTO.getNombre())) {
            throw new DataIntegrityViolationException("Ya existe otro operador con el nombre: " + operadorRequestDTO.getNombre());
        }
        
        operadorMapper.updateEntityFromDTO(operadorRequestDTO, operador);
        return operadorMapper.toResponseDTO(operadorRepository.save(operador));
    }

    @Override
    public void deleteById(int id) {
        if (!operadorRepository.existsById(id))
            throw new ResourceNotFoundException("Operador no encontrado con ID: " + id);
        operadorRepository.deleteById(id);
    }

    private List<OperadorResponseDTO> mapToResponseList(List<Operador> operadores) {
        return operadores.stream().map(operadorMapper::toResponseDTO).toList();
    }
}
