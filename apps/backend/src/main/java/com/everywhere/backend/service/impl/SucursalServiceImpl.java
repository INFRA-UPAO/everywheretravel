package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.SucursalMapper;
import com.everywhere.backend.model.dto.SucursalRequestDTO;
import com.everywhere.backend.model.dto.SucursalResponseDTO;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.SucursalRepository;
import com.everywhere.backend.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final SucursalMapper sucursalMapper;

    @Override
    public List<SucursalResponseDTO> findAll() {
        return mapToResponseList(sucursalRepository.findAll());
    }

    @Override
    public SucursalResponseDTO findById(Integer id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));
        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Override
    public List<SucursalResponseDTO> findByDescripcion(String descripcion) {
        return mapToResponseList(sucursalRepository.findByDescripcionContainingIgnoreCase(descripcion));
    }

    @Override
    public SucursalResponseDTO findByDescripcionExacta(String descripcion) {
        Sucursal sucursal = sucursalRepository.findByDescripcionIgnoreCase(descripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con descripción: " + descripcion));
        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Override
    public List<SucursalResponseDTO> findByEstado(Boolean estado) {
        return mapToResponseList(sucursalRepository.findByEstado(estado));
    }

    @Override
    public List<SucursalResponseDTO> findByEstadoAndDescripcion(Boolean estado, String descripcion) {
        return mapToResponseList(sucursalRepository.findByEstadoAndDescripcionContainingIgnoreCase(estado, descripcion));
    }

    @Override
    public List<SucursalResponseDTO> findByDireccion(String direccion) {
        return mapToResponseList(sucursalRepository.findByDireccionContainingIgnoreCase(direccion));
    }

    @Override
    public SucursalResponseDTO findByEmail(String email) {
        Sucursal sucursal = sucursalRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con email: " + email));
        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Override
    public SucursalResponseDTO save(SucursalRequestDTO sucursalRequestDTO) {
        if (sucursalRequestDTO.getEmail() != null &&
                !sucursalRequestDTO.getEmail().trim().isEmpty() &&
                sucursalRepository.existsByEmail(sucursalRequestDTO.getEmail())) {
            throw new DataIntegrityViolationException("Ya existe una sucursal con el email: " + sucursalRequestDTO.getEmail());
        }

        Sucursal sucursal = sucursalMapper.toEntity(sucursalRequestDTO);
        if (sucursal.getEstado() == null) sucursal.setEstado(true);

        return sucursalMapper.toResponseDTO(sucursalRepository.save(sucursal));
    }

    @Override
    public SucursalResponseDTO update(Integer id, SucursalRequestDTO sucursalRequestDTO) {
        if (!sucursalRepository.existsById(id))
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + id);

        Sucursal existing = sucursalRepository.findById(id).get();

        if (sucursalRequestDTO.getEmail() != null &&
                !sucursalRequestDTO.getEmail().trim().isEmpty() &&
                !sucursalRequestDTO.getEmail().equals(existing.getEmail()) &&
                sucursalRepository.existsByEmail(sucursalRequestDTO.getEmail())) {
            throw new BadRequestException("Ya existe una sucursal con el email: " + sucursalRequestDTO.getEmail());
        }

        sucursalMapper.updateEntityFromDTO(sucursalRequestDTO, existing);
        return sucursalMapper.toResponseDTO(sucursalRepository.save(existing));
    }

    @Override
    public void deleteById(Integer id) {
        if (!sucursalRepository.existsById(id))
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + id);
        sucursalRepository.deleteById(id);
    }

    @Override
    public SucursalResponseDTO cambiarEstado(Integer id, Boolean estado) {
        if (!sucursalRepository.existsById(id))
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + id);

        Sucursal sucursal = sucursalRepository.findById(id).get();
        sucursal.setEstado(estado);
        return sucursalMapper.toResponseDTO(sucursalRepository.save(sucursal));
    }

    private List<SucursalResponseDTO> mapToResponseList(List<Sucursal> sucursales) {
        return sucursales.stream().map(sucursalMapper::toResponseDTO).toList();
    }
}