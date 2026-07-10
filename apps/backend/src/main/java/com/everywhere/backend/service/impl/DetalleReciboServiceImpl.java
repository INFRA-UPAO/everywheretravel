package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DetalleReciboMapper;
import com.everywhere.backend.model.dto.DetalleReciboRequestDTO;
import com.everywhere.backend.model.dto.DetalleReciboResponseDTO;
import com.everywhere.backend.model.entity.DetalleRecibo;
import com.everywhere.backend.repository.DetalleReciboRepository;
import com.everywhere.backend.repository.ReciboRepository;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.service.DetalleReciboService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetalleReciboServiceImpl implements DetalleReciboService {

    private final DetalleReciboRepository detalleReciboRepository;
    private final ReciboRepository reciboRepository;
    private final ProductoRepository productoRepository;
    private final DetalleReciboMapper detalleReciboMapper;

    @Override
    public List<DetalleReciboResponseDTO> findAll() {
        return mapToResponseList(detalleReciboRepository.findAllWithRelations());
    }

    @Override
    public DetalleReciboResponseDTO findById(Integer id) {
        DetalleRecibo detalle = detalleReciboRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + id));
        return detalleReciboMapper.toResponseDTO(detalle);
    }

    @Override
    public List<DetalleReciboResponseDTO> findByReciboId(Integer reciboId) {
        if (!reciboRepository.existsById(reciboId))
            throw new ResourceNotFoundException("Recibo no encontrado con ID: " + reciboId);
        return mapToResponseList(detalleReciboRepository.findByReciboIdWithRelations(reciboId));
    }

    @Override
    @Transactional
    public DetalleReciboResponseDTO save(DetalleReciboRequestDTO detalleReciboRequestDTO) {
        if (!reciboRepository.existsById(detalleReciboRequestDTO.getReciboId()))
            throw new ResourceNotFoundException("Recibo no encontrado con ID: " + detalleReciboRequestDTO.getReciboId());
        
        if (detalleReciboRequestDTO.getProductoId() != null && !productoRepository.existsById(detalleReciboRequestDTO.getProductoId()))
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + detalleReciboRequestDTO.getProductoId());

        DetalleRecibo detalleRecibo = detalleReciboMapper.toEntity(detalleReciboRequestDTO);
        detalleRecibo.setRecibo(reciboRepository.findById(detalleReciboRequestDTO.getReciboId()).get());
        
        if (detalleReciboRequestDTO.getProductoId() != null) {
            detalleRecibo.setProducto(productoRepository.findById(detalleReciboRequestDTO.getProductoId()).get());
        }
 
        return detalleReciboMapper.toResponseDTO(detalleReciboRepository.save(detalleRecibo));
    }

    @Override
    @Transactional
    public DetalleReciboResponseDTO patch(Integer id, DetalleReciboRequestDTO detalleReciboRequestDTO) {
        if (!detalleReciboRepository.existsById(id))
            throw new ResourceNotFoundException("Detalle no encontrado con ID: " + id);

        DetalleRecibo detalleRecibo = detalleReciboRepository.findById(id).get();
        detalleReciboMapper.updateEntityFromRequest(detalleRecibo, detalleReciboRequestDTO);

        if (detalleReciboRequestDTO.getReciboId() != null) {
            if (!reciboRepository.existsById(detalleReciboRequestDTO.getReciboId()))
                throw new ResourceNotFoundException("Recibo no encontrado con ID: " + detalleReciboRequestDTO.getReciboId());
            detalleRecibo.setRecibo(reciboRepository.findById(detalleReciboRequestDTO.getReciboId()).get());
        }

        if (detalleReciboRequestDTO.getProductoId() != null) {
            if (!productoRepository.existsById(detalleReciboRequestDTO.getProductoId()))
                throw new ResourceNotFoundException("Producto no encontrado con ID: " + detalleReciboRequestDTO.getProductoId());
            detalleRecibo.setProducto(productoRepository.findById(detalleReciboRequestDTO.getProductoId()).get());
        }
 
        return detalleReciboMapper.toResponseDTO(detalleReciboRepository.save(detalleRecibo));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!detalleReciboRepository.existsById(id)) 
            throw new ResourceNotFoundException("Detalle no encontrado con ID: " + id);
        detalleReciboRepository.deleteById(id);
    }

    private List<DetalleReciboResponseDTO> mapToResponseList(List<DetalleRecibo> detalles) {
        return detalles.stream().map(detalleReciboMapper::toResponseDTO).toList();
    }
}
