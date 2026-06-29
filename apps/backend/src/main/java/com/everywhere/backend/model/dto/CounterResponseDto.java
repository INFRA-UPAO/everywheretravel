package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CounterResponseDto {
    private int id;

    private String nombre;

    private Boolean estado;

    private String codigo;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;
}
