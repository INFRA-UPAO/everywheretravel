package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProveedorResponseDTO {

    private Integer id;
    private String nombre;
    private String nombreJuridico;
    private Integer ruc;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
}
