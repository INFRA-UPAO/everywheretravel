package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class DocumentoRequestDto {
    private String tipo;
    private String descripcion;
    private Boolean estado;
}