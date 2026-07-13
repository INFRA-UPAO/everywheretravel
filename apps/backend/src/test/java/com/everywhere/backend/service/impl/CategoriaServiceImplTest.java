package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CategoriaMapper;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
}
