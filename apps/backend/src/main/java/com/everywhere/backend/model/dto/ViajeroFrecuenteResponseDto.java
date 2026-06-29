package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViajeroFrecuenteResponseDto {
    private Integer id;
    private String areolinea;
    private String codigo;
    private ViajeroResponseDTO viajero;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
}