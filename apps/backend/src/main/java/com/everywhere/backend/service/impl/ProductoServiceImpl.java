package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ConflictException;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ProductoMapper;
import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO; 
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.repository.DetalleCotizacionRepository;
import com.everywhere.backend.repository.DetalleLiquidacionRepository;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors; 

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final DetalleLiquidacionRepository detalleLiquidacionRepository;

    @Override
    public ProductoResponseDTO create(ProductoRequestDTO productoRequestDTO) {
        Producto producto = productoMapper.toEntity(productoRequestDTO);
        return productoMapper.toResponseDTO(productoRepository.save(producto));
    }

    @Override
    public ProductoResponseDTO update(Integer id, ProductoRequestDTO productoRequestDTO) {
        if (!productoRepository.existsById(id))
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);

        Producto producto = productoRepository.findById(id).get();
        
        if (productoRequestDTO.getTipo() != null && 
            productoRepository.existsProductosByTipo(productoRequestDTO.getTipo()) &&
            !productoRequestDTO.getTipo().equals(producto.getTipo())) {
            throw new DataIntegrityViolationException("Ya existe un producto con el tipo: " + productoRequestDTO.getTipo());
        }

        productoMapper.updateEntityFromDTO(productoRequestDTO, producto);
        return productoMapper.toResponseDTO(productoRepository.save(producto));
    }

    @Override
    public ProductoResponseDTO getById(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return productoMapper.toResponseDTO(producto);
    }

    @Override
    public List<ProductoResponseDTO> getAll() {
        return mapToResponseList(productoRepository.findAll());
    }

    @Override
    public void delete(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }

        long cotizacionesCount = detalleCotizacionRepository.countByProductoId(id);
        if (cotizacionesCount > 0) {
            throw new ConflictException(
                    "No se puede eliminar este producto porque tiene " + cotizacionesCount + " cotización(es) asociada(s).",
                    "/api/v1/producto/" + id
            );
        }

        long liquidacionesCount = detalleLiquidacionRepository.countByProductoId(id);
        if (liquidacionesCount > 0) {
            throw new ConflictException(
                    "No se puede eliminar este producto porque tiene " + liquidacionesCount + " liquidación(es) asociada(s).",
                    "/api/v1/producto/" + id
            );
        }

        productoRepository.deleteById(id);
    }

    private List<ProductoResponseDTO> mapToResponseList(List<Producto> productos) {
        return productos.stream()
                .map(productoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

}
