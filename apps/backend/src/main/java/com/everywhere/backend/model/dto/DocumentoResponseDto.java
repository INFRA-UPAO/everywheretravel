package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentoResponseDto {
    private int id;
    private String tipo;
    private String descripcion;
    private Boolean estado;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
}
