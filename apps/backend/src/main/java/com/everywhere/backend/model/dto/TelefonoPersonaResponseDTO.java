package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TelefonoPersonaResponseDTO {

    private Integer id;
    private String numero;
    private String codigoPais;
    private String tipo;
    private String descripcion;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
}
