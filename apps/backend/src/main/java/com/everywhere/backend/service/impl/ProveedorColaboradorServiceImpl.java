package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ProveedorColaboradorMapper;
import com.everywhere.backend.model.dto.ProveedorColaboradorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorColaboradorResponseDTO;
import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.model.entity.ProveedorColaborador;
import com.everywhere.backend.repository.ProveedorColaboradorRepository;
import com.everywhere.backend.repository.ProveedorRepository;
import com.everywhere.backend.service.ProveedorColaboradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorColaboradorServiceImpl implements ProveedorColaboradorService {

    private final ProveedorColaboradorRepository repository;
    private final ProveedorRepository proveedorRepository;
    private final ProveedorColaboradorMapper mapper;

    @Override
    public List<ProveedorColaboradorResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public ProveedorColaboradorResponseDTO findById(Integer id) {
        ProveedorColaborador entity = repository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Colaborador de proveedor no encontrado con ID: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public List<ProveedorColaboradorResponseDTO> findByProveedorId(Integer proveedorId) {
        return repository.findByProveedorId(proveedorId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public ProveedorColaboradorResponseDTO save(ProveedorColaboradorRequestDTO dto) {
        ProveedorColaborador entity = mapper.toEntity(dto);

        // Validar y asignar proveedor si está presente
        if (dto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(
                            () -> new BadRequestException("Proveedor no encontrado con ID: " + dto.getProveedorId()));
            entity.setProveedor(proveedor);
        }

        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    public ProveedorColaboradorResponseDTO update(Integer id, ProveedorColaboradorRequestDTO dto) {
        ProveedorColaborador existing = repository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Colaborador de proveedor no encontrado con ID: " + id));

        mapper.updateEntityFromDTO(dto, existing);

        // Actualizar proveedor si se proporciona
        if (dto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(
                            () -> new BadRequestException("Proveedor no encontrado con ID: " + dto.getProveedorId()));
            existing.setProveedor(proveedor);
        }

        return mapper.toResponseDTO(repository.save(existing));
    }

    @Override
    public void deleteById(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Colaborador de proveedor no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}
