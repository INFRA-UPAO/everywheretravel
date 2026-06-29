package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductoResponseDTO {

    private int id;
    private String descripcion;
    private String tipo;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
}
