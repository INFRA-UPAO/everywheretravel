package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ConflictException;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.FormaPagoMapper;
import com.everywhere.backend.model.dto.FormaPagoRequestDTO;
import com.everywhere.backend.model.dto.FormaPagoResponseDTO;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.repository.FormaPagoRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FormaPagoServiceImplTest {

    @Mock
    private FormaPagoRepository formaPagoRepository;

    @Mock
    private CotizacionRepository cotizacionRepository;

    private FormaPagoMapper formaPagoMapper;

    private FormaPagoServiceImpl formaPagoService;

    @BeforeEach
    void setUp() {
        formaPagoMapper = new FormaPagoMapper();
        ReflectionTestUtils.setField(formaPagoMapper, "modelMapper", new ModelMapper());
        formaPagoService = new FormaPagoServiceImpl(formaPagoRepository, formaPagoMapper, cotizacionRepository);
    }

    private FormaPago formaPagoConId(int id, Integer codigo, String descripcion) {
        FormaPago formaPago = new FormaPago();
        formaPago.setId(id);
        formaPago.setCodigo(codigo);
        formaPago.setDescripcion(descripcion);
        return formaPago;
    }

    @Test
    void findAll_conFormasDePagoExistentes_devuelveListaMapeada() {
        given(formaPagoRepository.findAll()).willReturn(List.of(
                formaPagoConId(1, 10, "Efectivo"),
                formaPagoConId(2, 20, "Tarjeta")));

        List<FormaPagoResponseDTO> resultado = formaPagoService.findAll();

        assertThat(resultado)
                .extracting(FormaPagoResponseDTO::getDescripcion)
                .containsExactly("Efectivo", "Tarjeta");
    }

    @Test
    void findById_conIdExistente_devuelveFormaPagoMapeada() {
        given(formaPagoRepository.findById(1)).willReturn(Optional.of(formaPagoConId(1, 10, "Efectivo")));

        FormaPagoResponseDTO resultado = formaPagoService.findById(1);

        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getDescripcion()).isEqualTo("Efectivo");
    }

    @Test
    void findById_conIdInexistente_lanzaResourceNotFoundException() {
        given(formaPagoRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> formaPagoService.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByCodigo_conCodigoExistente_devuelveFormaPagoMapeada() {
        given(formaPagoRepository.findByCodigo(10)).willReturn(Optional.of(formaPagoConId(1, 10, "Efectivo")));

        FormaPagoResponseDTO resultado = formaPagoService.findByCodigo(10);

        assertThat(resultado.getCodigo()).isEqualTo(10);
    }

    @Test
    void findByCodigo_conCodigoInexistente_lanzaResourceNotFoundException() {
        given(formaPagoRepository.findByCodigo(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> formaPagoService.findByCodigo(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByDescripcion_conCoincidencias_devuelveListaMapeada() {
        given(formaPagoRepository.findByDescripcionContainingIgnoreCase("efec"))
                .willReturn(List.of(formaPagoConId(1, 10, "Efectivo")));

        List<FormaPagoResponseDTO> resultado = formaPagoService.findByDescripcion("efec");

        assertThat(resultado).extracting(FormaPagoResponseDTO::getDescripcion).containsExactly("Efectivo");
    }

    @Test
    void save_conDatosValidos_guardaYDevuelveFormaPagoMapeada() {
        FormaPagoRequestDTO request = new FormaPagoRequestDTO();
        request.setCodigo(10);
        request.setDescripcion("Efectivo");
        given(formaPagoRepository.save(any(FormaPago.class))).willReturn(formaPagoConId(1, 10, "Efectivo"));

        FormaPagoResponseDTO resultado = formaPagoService.save(request);

        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getCodigo()).isEqualTo(10);
    }

    @Test
    void update_conIdInexistente_lanzaResourceNotFoundException() {
        FormaPagoRequestDTO request = new FormaPagoRequestDTO();
        request.setCodigo(20);
        given(formaPagoRepository.existsById(99)).willReturn(false);

        assertThatThrownBy(() -> formaPagoService.update(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_conCodigoYaUsadoPorOtraFormaPago_lanzaDataIntegrityViolationException() {
        FormaPagoRequestDTO request = new FormaPagoRequestDTO();
        request.setCodigo(20);
        given(formaPagoRepository.existsById(1)).willReturn(true);
        given(formaPagoRepository.findById(1)).willReturn(Optional.of(formaPagoConId(1, 10, "Efectivo")));
        given(formaPagoRepository.existsByCodigo(20)).willReturn(true);

        assertThatThrownBy(() -> formaPagoService.update(1, request))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(formaPagoRepository, never()).save(any());
    }

    @Test
    void update_conCodigoSinCambios_actualizaCorrectamente() {
        FormaPagoRequestDTO request = new FormaPagoRequestDTO();
        request.setCodigo(10);
        request.setDescripcion("Efectivo actualizado");
        given(formaPagoRepository.existsById(1)).willReturn(true);
        given(formaPagoRepository.findById(1)).willReturn(Optional.of(formaPagoConId(1, 10, "Efectivo")));
        given(formaPagoRepository.save(any(FormaPago.class))).willReturn(formaPagoConId(1, 10, "Efectivo actualizado"));

        FormaPagoResponseDTO resultado = formaPagoService.update(1, request);

        assertThat(resultado.getDescripcion()).isEqualTo("Efectivo actualizado");
    }

    @Test
    void deleteById_conIdInexistente_lanzaResourceNotFoundException() {
        given(formaPagoRepository.existsById(99)).willReturn(false);

        assertThatThrownBy(() -> formaPagoService.deleteById(99))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(formaPagoRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_conCotizacionesAsociadas_lanzaConflictException() {
        given(formaPagoRepository.existsById(1)).willReturn(true);
        given(cotizacionRepository.countByFormaPagoId(1)).willReturn(2L);

        assertThatThrownBy(() -> formaPagoService.deleteById(1))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("2 cotización(es)");

        verify(formaPagoRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_sinCotizacionesAsociadas_eliminaLaFormaPago() {
        given(formaPagoRepository.existsById(1)).willReturn(true);
        given(cotizacionRepository.countByFormaPagoId(1)).willReturn(0L);

        formaPagoService.deleteById(1);

        verify(formaPagoRepository, times(1)).deleteById(1);
    }
}
