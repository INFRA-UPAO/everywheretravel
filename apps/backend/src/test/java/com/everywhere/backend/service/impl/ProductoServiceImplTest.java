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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Mock
    private DetalleLiquidacionRepository detalleLiquidacionRepository;

    private ProductoMapper productoMapper;

    private ProductoServiceImpl productoService;

    @BeforeEach
    void setUp() {
        productoMapper = new ProductoMapper(new ModelMapper());
        productoService = new ProductoServiceImpl(
                productoRepository, productoMapper, detalleCotizacionRepository, detalleLiquidacionRepository);
    }

    private Producto productoConId(int id, String descripcion, String tipo) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setDescripcion(descripcion);
        producto.setTipo(tipo);
        return producto;
    }
}