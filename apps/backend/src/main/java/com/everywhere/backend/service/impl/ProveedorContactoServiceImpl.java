package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ProveedorContactoMapper;
import com.everywhere.backend.model.dto.ProveedorContactoRequestDTO;
import com.everywhere.backend.model.dto.ProveedorContactoResponseDTO;
import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.model.entity.ProveedorContacto;
import com.everywhere.backend.model.entity.ProveedorGrupoContacto;
import com.everywhere.backend.repository.ProveedorContactoRepository;
import com.everywhere.backend.repository.ProveedorRepository;
import com.everywhere.backend.repository.ProveedorGrupoContactoRepository;
import com.everywhere.backend.service.ProveedorContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorContactoServiceImpl implements ProveedorContactoService {

    private final ProveedorContactoRepository repository;
    private final ProveedorRepository proveedorRepository;
    private final ProveedorGrupoContactoRepository grupoContactoRepository;
    private final ProveedorContactoMapper mapper;

    @Override
    public List<ProveedorContactoResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public ProveedorContactoResponseDTO findById(Integer id) {
        ProveedorContacto entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contacto de proveedor no encontrado con ID: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public List<ProveedorContactoResponseDTO> findByProveedorId(Integer proveedorId) {
        return repository.findByProveedorId(proveedorId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<ProveedorContactoResponseDTO> findByGrupoContactoId(Integer grupoContactoId) {
        return repository.findByGrupoContactoId(grupoContactoId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public ProveedorContactoResponseDTO save(ProveedorContactoRequestDTO dto) {
        ProveedorContacto entity = mapper.toEntity(dto);

        // Validar y asignar proveedor si está presente
        if (dto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(
                            () -> new BadRequestException("Proveedor no encontrado con ID: " + dto.getProveedorId()));
            entity.setProveedor(proveedor);
        }

        // Validar y asignar grupo de contacto si está presente
        if (dto.getGrupoContactoId() != null) {
            ProveedorGrupoContacto grupo = grupoContactoRepository.findById(dto.getGrupoContactoId())
                    .orElseThrow(() -> new BadRequestException(
                            "Grupo de contacto no encontrado con ID: " + dto.getGrupoContactoId()));
            entity.setGrupoContacto(grupo);
        }

        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    public ProveedorContactoResponseDTO update(Integer id, ProveedorContactoRequestDTO dto) {
        ProveedorContacto existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contacto de proveedor no encontrado con ID: " + id));

        mapper.updateEntityFromDTO(dto, existing);

        // Actualizar proveedor si se proporciona
        if (dto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(
                            () -> new BadRequestException("Proveedor no encontrado con ID: " + dto.getProveedorId()));
            existing.setProveedor(proveedor);
        }

        // Actualizar grupo si se proporciona
        if (dto.getGrupoContactoId() != null) {
            ProveedorGrupoContacto grupo = grupoContactoRepository.findById(dto.getGrupoContactoId())
                    .orElseThrow(() -> new BadRequestException(
                            "Grupo de contacto no encontrado con ID: " + dto.getGrupoContactoId()));
            existing.setGrupoContacto(grupo);
        }

        return mapper.toResponseDTO(repository.save(existing));
    }

    @Override
    public void deleteById(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Contacto de proveedor no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}
