package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CotizacionRequestDto {
    private String nombreCotizacion;
    private Integer cantAdultos;
    private Integer cantNinos;
    private LocalDateTime fechaVencimiento;
    private String origenDestino;
    private LocalDate fechaSalida;
    private LocalDate fechaRegreso;
    private String moneda;
    private String observacion;

    private Integer counterId;
    private Integer formaPagoId;
    private Integer estadoCotizacionId;
    private Integer sucursalId;
    private Integer carpetaId;
}
