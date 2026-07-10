package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperadorResponseDTO {

    private Integer id;
    private String nombre;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
}
