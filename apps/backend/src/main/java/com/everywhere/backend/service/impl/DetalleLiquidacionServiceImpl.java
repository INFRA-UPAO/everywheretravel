package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.DetalleLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionSinLiquidacionDTO;
import com.everywhere.backend.model.entity.DetalleLiquidacion;
import com.everywhere.backend.repository.DetalleLiquidacionRepository;
import com.everywhere.backend.repository.LiquidacionRepository;
import com.everywhere.backend.repository.OperadorRepository;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.repository.ProveedorRepository;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.service.DetalleLiquidacionService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DetalleLiquidacionMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetalleLiquidacionServiceImpl implements DetalleLiquidacionService {

    private final DetalleLiquidacionRepository detalleLiquidacionRepository;
    private final DetalleLiquidacionMapper detalleLiquidacionMapper;
    private final LiquidacionRepository liquidacionRepository;
    private final OperadorRepository operadorRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final ViajeroRepository viajeroRepository;


    @Override
    public List<DetalleLiquidacionResponseDTO> findAll() {
        return mapToResponseList(detalleLiquidacionRepository.findAllWithRelations());
    }

    @Override
    public DetalleLiquidacionResponseDTO findById(Integer id) {
        DetalleLiquidacion detalleLiquidacion = detalleLiquidacionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id));
        return detalleLiquidacionMapper.toResponseDTO(detalleLiquidacion);
    }

    @Override
    public List<DetalleLiquidacionResponseDTO> findByLiquidacionId(Integer liquidacionId) {
        return mapToResponseList(detalleLiquidacionRepository.findByLiquidacionIdWithRelations(liquidacionId));
    }

    @Override
    public List<DetalleLiquidacionSinLiquidacionDTO> findByLiquidacionIdSinLiquidacion(Integer liquidacionId) {
        return mapToSinLiquidacionList(detalleLiquidacionRepository.findByLiquidacionIdSinLiquidacion(liquidacionId));
    }

    @Override
    public DetalleLiquidacionResponseDTO save(DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacion detalleLiquidacion = detalleLiquidacionMapper.toEntity(detalleLiquidacionRequestDTO);

        if(detalleLiquidacionRequestDTO.getLiquidacionId() != null) {
            if (!liquidacionRepository.existsById(detalleLiquidacionRequestDTO.getLiquidacionId()))
                throw new ResourceNotFoundException("Liquidación no encontrada con ID: " + detalleLiquidacionRequestDTO.getLiquidacionId());
            detalleLiquidacion.setLiquidacion(liquidacionRepository.findById(detalleLiquidacionRequestDTO.getLiquidacionId()).get());
        }

        if (detalleLiquidacionRequestDTO.getOperadorId() != null) {
            if (!operadorRepository.existsById(detalleLiquidacionRequestDTO.getOperadorId()))
                throw new ResourceNotFoundException("Operador no encontrado con ID: " + detalleLiquidacionRequestDTO.getOperadorId());
            detalleLiquidacion.setOperador(operadorRepository.findById(detalleLiquidacionRequestDTO.getOperadorId()).get());
        }

        if (detalleLiquidacionRequestDTO.getProveedorId() != null) {
            if (!proveedorRepository.existsById(detalleLiquidacionRequestDTO.getProveedorId()))
                throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + detalleLiquidacionRequestDTO.getProveedorId());
            detalleLiquidacion.setProveedor(proveedorRepository.findById(detalleLiquidacionRequestDTO.getProveedorId()).get());
        }

        if (detalleLiquidacionRequestDTO.getProductoId() != null) {
            if (!productoRepository.existsById(detalleLiquidacionRequestDTO.getProductoId()))
                throw new ResourceNotFoundException("Producto no encontrado con ID: " + detalleLiquidacionRequestDTO.getProductoId());
            detalleLiquidacion.setProducto(productoRepository.findById(detalleLiquidacionRequestDTO.getProductoId()).get());
        }

        if (detalleLiquidacionRequestDTO.getViajeroId() != null) {
            if (!viajeroRepository.existsById(detalleLiquidacionRequestDTO.getViajeroId()))
                throw new ResourceNotFoundException("Viajero no encontrado con ID: " + detalleLiquidacionRequestDTO.getViajeroId());
            detalleLiquidacion.setViajero(viajeroRepository.findById(detalleLiquidacionRequestDTO.getViajeroId()).get());
        }

        return detalleLiquidacionMapper.toResponseDTO(detalleLiquidacionRepository.save(detalleLiquidacion));
    }

    @Override
    public DetalleLiquidacionResponseDTO update(Integer id, DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        if (!detalleLiquidacionRepository.existsById(id))
            throw new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id);

        DetalleLiquidacion detalleLiquidacion = detalleLiquidacionRepository.findById(id).get();
        detalleLiquidacionMapper.updateEntityFromDTO(detalleLiquidacionRequestDTO, detalleLiquidacion);

        if(detalleLiquidacionRequestDTO.getLiquidacionId() != null) {
            if (!liquidacionRepository.existsById(detalleLiquidacionRequestDTO.getLiquidacionId()))
                throw new ResourceNotFoundException("Liquidación no encontrada con ID: " + detalleLiquidacionRequestDTO.getLiquidacionId());
            detalleLiquidacion.setLiquidacion(liquidacionRepository.findById(detalleLiquidacionRequestDTO.getLiquidacionId()).get());
        }

        if (detalleLiquidacionRequestDTO.getOperadorId() != null) {
            if (!operadorRepository.existsById(detalleLiquidacionRequestDTO.getOperadorId()))
                throw new ResourceNotFoundException("Operador no encontrado con ID: " + detalleLiquidacionRequestDTO.getOperadorId());
            detalleLiquidacion.setOperador(operadorRepository.findById(detalleLiquidacionRequestDTO.getOperadorId()).get());
        }

        if (detalleLiquidacionRequestDTO.getProveedorId() != null) {
            if (!proveedorRepository.existsById(detalleLiquidacionRequestDTO.getProveedorId()))
                throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + detalleLiquidacionRequestDTO.getProveedorId());
            detalleLiquidacion.setProveedor(proveedorRepository.findById(detalleLiquidacionRequestDTO.getProveedorId()).get());
        }

        if (detalleLiquidacionRequestDTO.getProductoId() != null) {
            if (!productoRepository.existsById(detalleLiquidacionRequestDTO.getProductoId()))
                throw new ResourceNotFoundException("Producto no encontrado con ID: " + detalleLiquidacionRequestDTO.getProductoId());
            detalleLiquidacion.setProducto(productoRepository.findById(detalleLiquidacionRequestDTO.getProductoId()).get());
        }

        if (detalleLiquidacionRequestDTO.getViajeroId() != null) {
            if (!viajeroRepository.existsById(detalleLiquidacionRequestDTO.getViajeroId()))
                throw new ResourceNotFoundException("Viajero no encontrado con ID: " + detalleLiquidacionRequestDTO.getViajeroId());
            detalleLiquidacion.setViajero(viajeroRepository.findById(detalleLiquidacionRequestDTO.getViajeroId()).get());
        }

        return detalleLiquidacionMapper.toResponseDTO(detalleLiquidacionRepository.save(detalleLiquidacion));
    }

    @Override
    public void deleteById(Integer id) {
        if (!detalleLiquidacionRepository.existsById(id)) 
            throw new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id);
        detalleLiquidacionRepository.deleteById(id);
    }

    private List<DetalleLiquidacionResponseDTO> mapToResponseList(List<DetalleLiquidacion> detalles) {
        return detalles.stream().map(detalleLiquidacionMapper::toResponseDTO).toList();
    }

    private List<DetalleLiquidacionSinLiquidacionDTO> mapToSinLiquidacionList(List<DetalleLiquidacion> detalles) {
        return detalles.stream().map(detalleLiquidacionMapper::toSinLiquidacionDTO).toList();
    }
}