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

    @Test
    void create_conDatosValidos_guardaYDevuelveProductoMapeado() {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setDescripcion("Seguro de viaje");
        request.setTipo("Seguro");
        given(productoRepository.save(any(Producto.class))).willReturn(productoConId(1, "Seguro de viaje", "Seguro"));

        ProductoResponseDTO resultado = productoService.create(request);

        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getTipo()).isEqualTo("Seguro");
    }

    @Test
    void update_conIdInexistente_lanzaResourceNotFoundException() {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setTipo("Seguro");
        given(productoRepository.existsById(99)).willReturn(false);

        assertThatThrownBy(() -> productoService.update(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_conTipoYaUsadoPorOtroProducto_lanzaDataIntegrityViolationException() {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setTipo("Hospedaje");
        given(productoRepository.existsById(1)).willReturn(true);
        given(productoRepository.findById(1)).willReturn(Optional.of(productoConId(1, "Vuelo directo", "Vuelo")));
        given(productoRepository.existsProductosByTipo("Hospedaje")).willReturn(true);

        assertThatThrownBy(() -> productoService.update(1, request))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(productoRepository, never()).save(any());
    }

    @Test
    void update_conTipoSinCambios_actualizaCorrectamente() {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setDescripcion("Vuelo directo actualizado");
        request.setTipo("Vuelo");
        given(productoRepository.existsById(1)).willReturn(true);
        given(productoRepository.findById(1)).willReturn(Optional.of(productoConId(1, "Vuelo directo", "Vuelo")));
        given(productoRepository.existsProductosByTipo("Vuelo")).willReturn(false);
        given(productoRepository.save(any(Producto.class)))
                .willReturn(productoConId(1, "Vuelo directo actualizado", "Vuelo"));

        ProductoResponseDTO resultado = productoService.update(1, request);

        assertThat(resultado.getDescripcion()).isEqualTo("Vuelo directo actualizado");
    }
}