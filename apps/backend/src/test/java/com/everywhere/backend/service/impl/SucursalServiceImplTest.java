package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.SucursalMapper;
import com.everywhere.backend.model.dto.SucursalResponseDTO;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SucursalServiceImplTest {

    @Mock
    private SucursalRepository sucursalRepository;

    private SucursalMapper sucursalMapper;

    private SucursalServiceImpl sucursalService;

    @BeforeEach
    void setUp() {
        sucursalMapper = new SucursalMapper();
        ReflectionTestUtils.setField(sucursalMapper, "modelMapper", new ModelMapper());
        sucursalService = new SucursalServiceImpl(sucursalRepository, sucursalMapper);
    }

    private Sucursal sucursalConId(int id, String descripcion, String email, Boolean estado) {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(id);
        sucursal.setDescripcion(descripcion);
        sucursal.setEmail(email);
        sucursal.setEstado(estado);
        return sucursal;
    }

    @Test
    void findAll_conSucursalesExistentes_devuelveListaMapeada() {
        given(sucursalRepository.findAll()).willReturn(List.of(
                sucursalConId(1, "Sucursal Centro", "centro@everywhere.com", true),
                sucursalConId(2, "Sucursal Norte", "norte@everywhere.com", true)));

        List<SucursalResponseDTO> resultado = sucursalService.findAll();

        assertThat(resultado).extracting(SucursalResponseDTO::getDescripcion)
                .containsExactly("Sucursal Centro", "Sucursal Norte");
    }

    @Test
    void findById_conIdExistente_devuelveSucursalMapeada() {
        given(sucursalRepository.findById(1))
                .willReturn(Optional.of(sucursalConId(1, "Sucursal Centro", "centro@everywhere.com", true)));

        SucursalResponseDTO resultado = sucursalService.findById(1);

        assertThat(resultado.getDescripcion()).isEqualTo("Sucursal Centro");
    }

    @Test
    void findById_conIdInexistente_lanzaResourceNotFoundException() {
        given(sucursalRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sucursalService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}