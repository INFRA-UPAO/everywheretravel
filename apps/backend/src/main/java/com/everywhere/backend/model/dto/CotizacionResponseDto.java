package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CotizacionResponseDto {

    private int id;
    private String nombreCotizacion;
    private String codigoCotizacion;
    private int cantAdultos;
    private int cantNinos;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaVencimiento;
    private LocalDateTime actualizado;
    private String origenDestino;
    private LocalDate fechaSalida;
    private LocalDate fechaRegreso;
    private String moneda;
    private String observacion;

    private CounterResponseDto counter;
    private FormaPagoResponseDTO formaPago;
    private EstadoCotizacionResponseDTO estadoCotizacion;
    private SucursalResponseDTO sucursal;
    private CarpetaResponseDto carpeta;
    private PersonaResponseDTO personas;
}
