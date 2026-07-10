package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ConflictException;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ProveedorMapper;
import com.everywhere.backend.model.dto.ProveedorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorResponseDTO;
import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.repository.DetalleCotizacionRepository;
import com.everywhere.backend.repository.DetalleLiquidacionRepository;
import com.everywhere.backend.repository.ProveedorRepository;
import com.everywhere.backend.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final DetalleLiquidacionRepository detalleLiquidacionRepository;

    @Override
    public ProveedorResponseDTO create(ProveedorRequestDTO proveedorRequestDTO) {
        if (proveedorRequestDTO.getRuc() != null && proveedorRepository.existsByRuc(proveedorRequestDTO.getRuc()))
            throw new DataIntegrityViolationException("Ya existe un proveedor con el RUC: " + proveedorRequestDTO.getRuc());

        Proveedor proveedor = proveedorMapper.toEntity(proveedorRequestDTO);
        return proveedorMapper.toResponseDTO(proveedorRepository.save(proveedor));
    }

    @Override
    public ProveedorResponseDTO update(Integer id, ProveedorRequestDTO proveedorRequestDTO) {
        if (!proveedorRepository.existsById(id))
            throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + id);

        Proveedor proveedor = proveedorRepository.findById(id).get();
        
        if (proveedorRequestDTO.getRuc() != null && 
            proveedorRepository.existsByRuc(proveedorRequestDTO.getRuc()) &&
            !proveedorRequestDTO.getRuc().equals(proveedor.getRuc())) {
            throw new DataIntegrityViolationException("Ya existe un proveedor con el RUC: " + proveedorRequestDTO.getRuc());
        }

        proveedorMapper.updateEntityFromDTO(proveedorRequestDTO, proveedor);
        return proveedorMapper.toResponseDTO(proveedorRepository.save(proveedor));
    }

    @Override
    public ProveedorResponseDTO getById(Integer id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
        return proveedorMapper.toResponseDTO(proveedor);
    }

    @Override
    public List<ProveedorResponseDTO> getAll() {
        return mapToResponseList(proveedorRepository.findAll());
    }

    @Override
    public void delete(Integer id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + id);
        }

        long cotizacionesCount = detalleCotizacionRepository.countByProveedorId(id);
        if (cotizacionesCount > 0) {
            throw new ConflictException(
                    "No se puede eliminar este proveedor porque tiene " + cotizacionesCount + " cotización(es) asociada(s).",
                    "/api/v1/proveedores/" + id
            );
        }

        long liquidacionesCount = detalleLiquidacionRepository.countByProveedorId(id);
        if (liquidacionesCount > 0) {
            throw new ConflictException(
                    "No se puede eliminar este proveedor porque tiene " + liquidacionesCount + " liquidación(es) asociada(s).",
                    "/api/v1/proveedores/" + id
            );
        }


        proveedorRepository.deleteById(id);
    }

    private List<ProveedorResponseDTO> mapToResponseList(List<Proveedor> proveedores) {
        return proveedores.stream().map(proveedorMapper::toResponseDTO).toList();
    }
}