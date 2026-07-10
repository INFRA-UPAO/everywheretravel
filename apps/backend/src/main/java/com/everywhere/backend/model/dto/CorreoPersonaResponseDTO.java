package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CorreoPersonaResponseDTO {

    private Integer id;
    private String email;
    private String tipo;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
}
