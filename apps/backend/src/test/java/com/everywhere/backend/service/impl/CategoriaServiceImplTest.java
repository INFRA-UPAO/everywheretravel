package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.CategoriaMapper;
import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.CategoriaResponseDto;
import com.everywhere.backend.model.entity.Categoria;
import com.everywhere.backend.repository.CategoriaRepository;
import com.everywhere.backend.repository.DetalleCotizacionRepository;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private DetalleCotizacionRepository detalleCotizacionRepository;

    private CategoriaMapper categoriaMapper;

    private CategoriaServiceImpl categoriaService;

    @BeforeEach
    void setUp() {
        categoriaMapper = new CategoriaMapper(new ModelMapper());
        categoriaService = new CategoriaServiceImpl(categoriaRepository, categoriaMapper, detalleCotizacionRepository);
    }

    private Categoria categoriaConId(int id, String nombre) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre(nombre);
        return categoria;
    }

    @Test
    void findAll_conCategoriasExistentes_devuelveListaMapeada() {
        given(categoriaRepository.findAll()).willReturn(List.of(
                categoriaConId(1, "Hoteles"),
                categoriaConId(2, "Vuelos")));

        List<CategoriaResponseDto> resultado = categoriaService.findAll();

        assertThat(resultado)
                .extracting(CategoriaResponseDto::getNombre)
                .containsExactly("Hoteles", "Vuelos");
    }

    @Test
    void findById_conIdExistente_devuelveCategoriaMapeada() {
        given(categoriaRepository.findById(1)).willReturn(Optional.of(categoriaConId(1, "Hoteles")));

        CategoriaResponseDto resultado = categoriaService.findById(1);

        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNombre()).isEqualTo("Hoteles");
    }

    @Test
    void findById_conIdInexistente_lanzaResourceNotFoundException() {
        given(categoriaRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_conNombreUnico_guardaYDevuelveCategoriaMapeada() {
        CategoriaRequestDto request = new CategoriaRequestDto();
        request.setNombre("Hoteles");
        given(categoriaRepository.existsByNombreIgnoreCase("Hoteles")).willReturn(false);
        given(categoriaRepository.save(any(Categoria.class))).willReturn(categoriaConId(1, "Hoteles"));

        CategoriaResponseDto resultado = categoriaService.create(request);

        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNombre()).isEqualTo("Hoteles");
    }

    @Test
    void create_conNombreDuplicado_lanzaDataIntegrityViolationException() {
        CategoriaRequestDto request = new CategoriaRequestDto();
        request.setNombre("Hoteles");
        given(categoriaRepository.existsByNombreIgnoreCase("Hoteles")).willReturn(true);

        assertThatThrownBy(() -> categoriaService.create(request))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(categoriaRepository, never()).save(any());
    }
}
