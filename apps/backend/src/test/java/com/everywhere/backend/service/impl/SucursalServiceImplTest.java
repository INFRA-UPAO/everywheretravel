package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.SucursalMapper;
import com.everywhere.backend.model.dto.SucursalRequestDTO;
import com.everywhere.backend.model.dto.SucursalResponseDTO;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

    @Test
    void save_conEmailDuplicado_lanzaDataIntegrityViolationException() {
        SucursalRequestDTO request = new SucursalRequestDTO();
        request.setDescripcion("Sucursal Sur");
        request.setEmail("centro@everywhere.com");
        given(sucursalRepository.existsByEmail("centro@everywhere.com")).willReturn(true);

        assertThatThrownBy(() -> sucursalService.save(request))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(sucursalRepository, never()).save(any());
    }

    @Test
    void save_conEmailUnico_guardaYDevuelveSucursalMapeada() {
        SucursalRequestDTO request = new SucursalRequestDTO();
        request.setDescripcion("Sucursal Sur");
        request.setEmail("sur@everywhere.com");
        given(sucursalRepository.existsByEmail("sur@everywhere.com")).willReturn(false);
        given(sucursalRepository.save(any(Sucursal.class)))
                .willReturn(sucursalConId(1, "Sucursal Sur", "sur@everywhere.com", true));

        SucursalResponseDTO resultado = sucursalService.save(request);

        assertThat(resultado.getEstado()).isTrue();
        assertThat(resultado.getDescripcion()).isEqualTo("Sucursal Sur");
    }

    @Test
    void update_conIdInexistente_lanzaResourceNotFoundException() {
        SucursalRequestDTO request = new SucursalRequestDTO();
        request.setEmail("nuevo@everywhere.com");
        given(sucursalRepository.existsById(99)).willReturn(false);

        assertThatThrownBy(() -> sucursalService.update(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_conEmailYaUsadoPorOtraSucursal_lanzaBadRequestException() {
        SucursalRequestDTO request = new SucursalRequestDTO();
        request.setEmail("norte@everywhere.com");
        given(sucursalRepository.existsById(1))
                .willReturn(true);
        given(sucursalRepository.findById(1))
                .willReturn(Optional.of(sucursalConId(1, "Sucursal Centro", "centro@everywhere.com", true)));
        given(sucursalRepository.existsByEmail("norte@everywhere.com")).willReturn(true);

        assertThatThrownBy(() -> sucursalService.update(1, request))
                .isInstanceOf(BadRequestException.class);

        verify(sucursalRepository, never()).save(any());
    }

    @Test
    void update_conEmailSinCambios_actualizaCorrectamente() {
        SucursalRequestDTO request = new SucursalRequestDTO();
        request.setEmail("centro@everywhere.com");
        request.setDescripcion("Sucursal Centro actualizada");
        given(sucursalRepository.existsById(1)).willReturn(true);
        given(sucursalRepository.findById(1))
                .willReturn(Optional.of(sucursalConId(1, "Sucursal Centro", "centro@everywhere.com", true)));
        given(sucursalRepository.save(any(Sucursal.class)))
                .willReturn(sucursalConId(1, "Sucursal Centro actualizada", "centro@everywhere.com", true));

        SucursalResponseDTO resultado = sucursalService.update(1, request);

        assertThat(resultado.getDescripcion()).isEqualTo("Sucursal Centro actualizada");
    }
}