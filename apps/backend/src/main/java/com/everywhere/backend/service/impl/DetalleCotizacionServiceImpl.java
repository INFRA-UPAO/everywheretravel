package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DetalleCotizacionMapper;
import com.everywhere.backend.model.dto.DetalleCotizacionRequestDto;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.model.entity.*;
import com.everywhere.backend.repository.*;
import com.everywhere.backend.service.DetalleCotizacionService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;  

@Service
@RequiredArgsConstructor
public class DetalleCotizacionServiceImpl implements DetalleCotizacionService {

    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final CotizacionRepository cotizacionRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final OperadorRepository operadorRepository;
    private final DetalleCotizacionMapper detalleCotizacionMapper;

    @Override
    public List<DetalleCotizacionResponseDto> findAll() {
        return mapToResponseList(detalleCotizacionRepository.findAll());
    }

    @Override
    public DetalleCotizacionResponseDto findById(Integer id) {
        return detalleCotizacionRepository.findById(id).map(detalleCotizacionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de cotización no encontrado con ID: " + id));
    }

    @Override
    public List<DetalleCotizacionResponseDto> findByCotizacionId(Integer cotizacionId) {
        if (!cotizacionRepository.existsById(cotizacionId))
            throw new ResourceNotFoundException("Cotización no encontrada con ID: " + cotizacionId);
        
        return mapToResponseList(detalleCotizacionRepository.findByCotizacionId(cotizacionId));
    }

    @Override
    public DetalleCotizacionResponseDto create(DetalleCotizacionRequestDto detalleCotizacionRequestDto, Integer cotizacionId) {
        if (cotizacionId == null) throw new IllegalArgumentException("El ID de la cotización es obligatorio");
        
        DetalleCotizacion detalleCotizacion = detalleCotizacionMapper.toEntity(detalleCotizacionRequestDto);

        if (!cotizacionRepository.existsById(cotizacionId))
            throw new ResourceNotFoundException("Cotización no encontrada con ID: " + cotizacionId);
        detalleCotizacion.setCotizacion(cotizacionRepository.findById(cotizacionId).get());

        if (detalleCotizacionRequestDto.getCategoriaId() != null) {
            if (!categoriaRepository.existsById(detalleCotizacionRequestDto.getCategoriaId()))
                throw new ResourceNotFoundException("Categoría no encontrada con ID: " + detalleCotizacionRequestDto.getCategoriaId());
            detalleCotizacion.setCategoria(categoriaRepository.findById(detalleCotizacionRequestDto.getCategoriaId()).get());
        }

        if (detalleCotizacionRequestDto.getProductoId() != null) {
            if (!productoRepository.existsById(detalleCotizacionRequestDto.getProductoId()))
                throw new ResourceNotFoundException("Producto no encontrado con ID: " + detalleCotizacionRequestDto.getProductoId());
            detalleCotizacion.setProducto(productoRepository.findById(detalleCotizacionRequestDto.getProductoId()).get());
        }

        if (detalleCotizacionRequestDto.getProveedorId() != null) {
            if (!proveedorRepository.existsById(detalleCotizacionRequestDto.getProveedorId()))
                throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + detalleCotizacionRequestDto.getProveedorId());
            detalleCotizacion.setProveedor(proveedorRepository.findById(detalleCotizacionRequestDto.getProveedorId()).get());
        }

        if (detalleCotizacionRequestDto.getOperadorId() != null) {
            if (!operadorRepository.existsById(detalleCotizacionRequestDto.getOperadorId()))
                throw new ResourceNotFoundException("Operador no encontrado con ID: " + detalleCotizacionRequestDto.getOperadorId());
            detalleCotizacion.setOperador(operadorRepository.findById(detalleCotizacionRequestDto.getOperadorId()).get());
        }

        return detalleCotizacionMapper.toResponse(detalleCotizacionRepository.save(detalleCotizacion));
    }

    @Override
    public DetalleCotizacionResponseDto patch(Integer id, DetalleCotizacionRequestDto detalleCotizacionRequestDto) {
        if (!detalleCotizacionRepository.existsById(id))
            throw new ResourceNotFoundException("Detalle de cotización no encontrado con ID: " + id);

        DetalleCotizacion detalleCotizacion = detalleCotizacionRepository.findById(id).get();
        detalleCotizacionMapper.updateEntityFromRequest(detalleCotizacion, detalleCotizacionRequestDto);

        if (detalleCotizacionRequestDto.getCategoriaId() != null) {
            if (!categoriaRepository.existsById(detalleCotizacionRequestDto.getCategoriaId()))
                throw new ResourceNotFoundException("Categoría no encontrada con ID: " + detalleCotizacionRequestDto.getCategoriaId());
            detalleCotizacion.setCategoria(categoriaRepository.findById(detalleCotizacionRequestDto.getCategoriaId()).get());
        }

        if (detalleCotizacionRequestDto.getProductoId() != null) {
            if (!productoRepository.existsById(detalleCotizacionRequestDto.getProductoId()))
                throw new ResourceNotFoundException("Producto no encontrado con ID: " + detalleCotizacionRequestDto.getProductoId());
            detalleCotizacion.setProducto(productoRepository.findById(detalleCotizacionRequestDto.getProductoId()).get());
        }

        if (detalleCotizacionRequestDto.getProveedorId() != null) {
            if (!proveedorRepository.existsById(detalleCotizacionRequestDto.getProveedorId()))
                throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + detalleCotizacionRequestDto.getProveedorId());
            detalleCotizacion.setProveedor(proveedorRepository.findById(detalleCotizacionRequestDto.getProveedorId()).get());
        }

        if (detalleCotizacionRequestDto.getOperadorId() != null) {
            if (!operadorRepository.existsById(detalleCotizacionRequestDto.getOperadorId()))
                throw new ResourceNotFoundException("Operador no encontrado con ID: " + detalleCotizacionRequestDto.getOperadorId());
            detalleCotizacion.setOperador(operadorRepository.findById(detalleCotizacionRequestDto.getOperadorId()).get());
        }

        return detalleCotizacionMapper.toResponse(detalleCotizacionRepository.save(detalleCotizacion));
    }

    @Override
    public void delete(Integer id) {
        if (!detalleCotizacionRepository.existsById(id))
            throw new ResourceNotFoundException("Detalle de cotización no encontrado con ID: " + id);
        detalleCotizacionRepository.deleteById(id);
    }

    private List<DetalleCotizacionResponseDto> mapToResponseList(List<DetalleCotizacion> detalles) {
        return detalles.stream().map(detalleCotizacionMapper::toResponse).toList();
    }
}